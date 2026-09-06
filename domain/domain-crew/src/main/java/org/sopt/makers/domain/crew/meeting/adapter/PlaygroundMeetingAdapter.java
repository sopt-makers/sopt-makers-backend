package org.sopt.makers.domain.crew.meeting.adapter;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.NOT_FOUND_MEETING;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;
import org.sopt.makers.domain.crew.meeting.port.MeetingActiveGenerationPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingApplyRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.crew.meeting.port.MemberRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.PlaygroundCrewStatsPort;
import org.sopt.makers.domain.crew.meeting.port.PlaygroundMeetingApplicantPort;
import org.sopt.makers.domain.crew.meeting.port.PlaygroundMemberCrewPort;
import org.sopt.makers.domain.crew.meeting.port.PlaygroundRelatedMeetingUserPort;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.sopt.makers.domain.user.Activity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaygroundMeetingAdapter
    implements PlaygroundMemberCrewPort,
        PlaygroundRelatedMeetingUserPort,
        PlaygroundCrewStatsPort,
        PlaygroundMeetingApplicantPort {

  private final MeetingService meetingService;
  private final MeetingRepositoryPort meetingRepositoryPort;
  private final MemberRepositoryPort memberRepositoryPort;
  private final MeetingApplyRepositoryPort applyRepositoryPort;
  private final MeetingUserPort meetingUserPort;
  private final MeetingActiveGenerationPort activeGenerationPort;
  private final Clock clock;

  @Override
  public PageResult<MemberCrewInfo> findUserMeetings(Long userId, int page, int take) {
    return meetingService
        .findJoinedMeetings(userId, page, take)
        .map(
            joined -> {
              Meeting meeting = joined.summary().meeting();
              MemberRole role = joined.member().role();
              return new MemberCrewInfo(
                  meeting.id(),
                  role == MemberRole.LEADER || role == MemberRole.CO_LEADER,
                  meeting.title(),
                  meeting.subTitle(),
                  firstImageUrl(meeting),
                  meeting.category().getValue(),
                  meeting.activityStartDate(),
                  meeting.activityEndDate(),
                  meeting.joinInfo(),
                  isActiveMeeting(meeting, LocalDateTime.now(clock)));
            });
  }

  @Override
  public RelatedMeetingUsers findRelatedUserIds(Long userId) {
    List<Long> meetingIds =
        memberRepositoryPort.findAllByUserId(userId).stream()
            .map(Member::meetingId)
            .distinct()
            .sorted(Comparator.reverseOrder())
            .toList();
    if (meetingIds.isEmpty()) {
      return new RelatedMeetingUsers(List.of(), List.of());
    }

    Map<Long, Meeting> meetingMap =
        meetingRepositoryPort.findAllByIds(meetingIds).stream()
            .collect(Collectors.toMap(Meeting::id, Function.identity()));
    int activeGeneration = activeGenerationPort.getActiveGeneration();
    LinkedHashSet<Long> currentGenerationUserIds = new LinkedHashSet<>();
    LinkedHashSet<Long> pastGenerationUserIds = new LinkedHashSet<>();

    memberRepositoryPort.findAllByMeetingIds(meetingIds).stream()
        .filter(member -> member.role() == MemberRole.PARTICIPANT)
        .filter(member -> !member.userId().equals(userId))
        .sorted(Comparator.comparing(Member::meetingId).reversed())
        .forEach(
            member -> {
              Meeting meeting = meetingMap.get(member.meetingId());
              if (meeting == null) {
                return;
              }
              if (meeting.createdGeneration() == activeGeneration) {
                currentGenerationUserIds.add(member.userId());
              } else {
                pastGenerationUserIds.add(member.userId());
              }
            });

    return new RelatedMeetingUsers(
        List.copyOf(currentGenerationUserIds), List.copyOf(pastGenerationUserIds));
  }

  @Override
  public List<FastestAppliedMeeting> findFastestAppliedMeetings(
      Long userId, int queryCount, int queryYear) {
    if (queryCount <= 0) {
      return List.of();
    }
    LocalDateTime startDate = Year.of(queryYear).atDay(1).atStartOfDay();
    LocalDateTime endDate = Year.of(queryYear).atMonth(12).atEndOfMonth().atTime(LocalTime.MAX);
    List<MeetingApply> applies =
        applyRepositoryPort.findAllByUserIdAndAppliedDateBetween(userId, startDate, endDate);
    Map<Long, Meeting> meetingMap =
        meetingRepositoryPort
            .findAllByIds(applies.stream().map(MeetingApply::meetingId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(Meeting::id, Function.identity()));

    return applies.stream()
        .filter(apply -> meetingMap.containsKey(apply.meetingId()))
        .sorted(
            Comparator.comparing(
                    (MeetingApply apply) ->
                        Duration.between(
                            meetingMap.get(apply.meetingId()).startDate(), apply.appliedDate()))
                .thenComparing(MeetingApply::id))
        .limit(queryCount)
        .map(
            apply -> {
              Meeting meeting = meetingMap.get(apply.meetingId());
              return new FastestAppliedMeeting(meeting.id(), meeting.title());
            })
        .toList();
  }

  @Override
  public PageResult<ApplicantInfo> findApplicants(
      Long meetingId, Long requesterUserId, ApplicantQuery query) {
    getMeeting(meetingId);
    boolean canReadContent =
        memberRepositoryPort
            .findByMeetingIdAndUserId(meetingId, requesterUserId)
            .map(Member::role)
            .map(role -> role == MemberRole.LEADER || role == MemberRole.CO_LEADER)
            .orElse(false);
    List<MeetingApply> applies =
        new ArrayList<>(
            applyRepositoryPort.findAllByMeetingIdAndStatuses(meetingId, query.statuses()));
    Comparator<MeetingApply> comparator = Comparator.comparing(MeetingApply::appliedDate);
    if (query.sortDirection() == SortDirection.DESC) {
      comparator = comparator.reversed();
    }
    applies.sort(comparator.thenComparing(MeetingApply::id));

    long requestedOffset = (long) (query.page() - 1) * query.take();
    int fromIndex = (int) Math.min(requestedOffset, applies.size());
    int toIndex = Math.min(fromIndex + query.take(), applies.size());
    List<MeetingApply> pageContent = applies.subList(fromIndex, toIndex);
    Map<Long, MeetingUser> userMap =
        meetingUserPort
            .findAllById(pageContent.stream().map(MeetingApply::userId).toList())
            .stream()
            .collect(Collectors.toMap(MeetingUser::id, Function.identity()));

    List<ApplicantInfo> applicants = new ArrayList<>();
    for (int index = 0; index < pageContent.size(); index++) {
      MeetingApply apply = pageContent.get(index);
      MeetingUser user = userMap.get(apply.userId());
      if (user == null) {
        continue;
      }
      Activity activity = user.findLatestActivity().orElse(null);
      applicants.add(
          new ApplicantInfo(
              apply.id(),
              fromIndex + index + 1,
              canReadContent ? apply.content() : "",
              apply.appliedDate(),
              apply.status(),
              new ApplicantUser(
                  user.id(),
                  user.name(),
                  user.id(),
                  activity == null
                      ? null
                      : new RecentActivity(
                          activity.part() == null ? null : activity.part().getName(),
                          activity.generation()),
                  user.profileImage(),
                  user.phone())));
    }

    int totalPages = (int) Math.ceil((double) applies.size() / query.take());
    return new PageResult<>(
        applicants,
        applies.size(),
        totalPages,
        query.page(),
        query.take(),
        query.page() < totalPages,
        query.page() > 1);
  }

  private Meeting getMeeting(Long meetingId) {
    return meetingRepositoryPort
        .findById(meetingId)
        .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));
  }

  private boolean isActiveMeeting(Meeting meeting, LocalDateTime now) {
    if (meeting.activityStartDate() == null || meeting.activityEndDate() == null) {
      return true;
    }
    return !now.isBefore(meeting.activityStartDate()) && now.isBefore(meeting.activityEndDate());
  }

  private String firstImageUrl(Meeting meeting) {
    return meeting.images().stream().findFirst().map(MeetingImage::url).orElse(null);
  }
}
