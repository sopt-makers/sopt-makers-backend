package org.sopt.makers.domain.crew.meeting.service;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.ALREADY_APPLIED_MEETING;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEETING_CATEGORY;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.MISSING_USER_ACTIVITY;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.NOT_ACTIVE_GENERATION;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.NOT_FOUND_APPLY;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.NOT_FOUND_MEETING;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.NOT_FOUND_USER;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.NOT_IN_APPLY_PERIOD;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.NOT_TARGET_PART;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.crew.meeting.CoLeader;
import org.sopt.makers.domain.crew.meeting.CoLeaders;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingApplies;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;
import org.sopt.makers.domain.crew.meeting.port.CoLeaderRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingApplyRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.user.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

  private final MeetingRepositoryPort meetingRepositoryPort;
  private final MeetingApplyRepositoryPort meetingApplyRepositoryPort;
  private final CoLeaderRepositoryPort coLeaderRepositoryPort;
  private final MeetingUserPort meetingUserPort;
  private final Clock clock;

  @Transactional
  public Meeting createMeeting(CreateMeetingCommand command, Long userId) {
    MeetingUser creator = getUser(userId);
    int createdGeneration = resolveCreatedGeneration(command.createdGeneration(), creator);
    Integer targetActiveGeneration =
        command.targetActiveGeneration() != null
            ? command.targetActiveGeneration()
            : Boolean.TRUE.equals(command.canJoinOnlyActiveGeneration()) ? createdGeneration : null;

    Meeting meeting =
        Meeting.create(
            userId,
            command.meetingDemandId(),
            command.title(),
            command.subTitle(),
            command.category(),
            command.images(),
            command.startDate(),
            command.endDate(),
            command.capacity(),
            command.description(),
            command.processDescription(),
            command.activityStartDate(),
            command.activityEndDate(),
            command.leaderDescription(),
            command.note(),
            command.isMentorNeeded(),
            command.canJoinOnlyActiveGeneration(),
            command.joinInfo(),
            createdGeneration,
            targetActiveGeneration,
            command.joinableParts());

    Meeting saved = meetingRepositoryPort.save(meeting);
    replaceCoLeaders(saved, command.coLeaderUserIds());
    return saved;
  }

  @Transactional
  public Meeting updateMeeting(Long meetingId, UpdateMeetingCommand command, Long userId) {
    Meeting meeting = getMeeting(meetingId);
    meeting.validateLeader(userId);

    Meeting.UpdateValues values =
        new Meeting.UpdateValues(
            command.title(),
            command.subTitle(),
            command.category(),
            command.images(),
            command.startDate(),
            command.endDate(),
            command.capacity(),
            command.description(),
            command.processDescription(),
            command.activityStartDate(),
            command.activityEndDate(),
            command.leaderDescription(),
            command.note(),
            command.isMentorNeeded(),
            command.canJoinOnlyActiveGeneration(),
            command.joinInfo(),
            command.targetActiveGeneration(),
            command.joinableParts());

    Meeting saved = meetingRepositoryPort.save(meeting.patch(values));
    if (command.coLeaderUserIds() != null) {
      replaceCoLeaders(saved, command.coLeaderUserIds());
    }
    return saved;
  }

  @Transactional
  public void deleteMeeting(Long meetingId, Long userId) {
    Meeting meeting = getMeeting(meetingId);
    meeting.validateLeader(userId);
    meetingApplyRepositoryPort.deleteAllByMeetingId(meetingId);
    coLeaderRepositoryPort.deleteAllByMeetingId(meetingId);
    meetingRepositoryPort.delete(meeting);
  }

  @Transactional
  public MeetingApply applyGeneralMeeting(ApplyMeetingCommand command, Long userId) {
    Meeting meeting = getMeeting(command.meetingId());
    if (meeting.category() == MeetingCategory.EVENT) {
      throw new MeetingException(INVALID_MEETING_CATEGORY);
    }
    return applyMeeting(meeting, command, userId);
  }

  @Transactional
  public MeetingApply applyEventMeeting(ApplyMeetingCommand command, Long userId) {
    Meeting meeting = getMeeting(command.meetingId());
    if (meeting.category() != MeetingCategory.EVENT) {
      throw new MeetingException(INVALID_MEETING_CATEGORY);
    }
    return applyMeeting(meeting, command, userId);
  }

  @Transactional
  public void cancelApply(Long meetingId, Long userId) {
    if (!meetingApplyRepositoryPort.existsByMeetingIdAndUserId(meetingId, userId)) {
      throw new MeetingException(NOT_FOUND_APPLY);
    }
    meetingApplyRepositoryPort.deleteByMeetingIdAndUserId(meetingId, userId);
  }

  @Transactional
  public MeetingApply updateApplyStatus(
      Long meetingId, UpdateApplyStatusCommand command, Long userId) {
    Meeting meeting = getMeeting(meetingId);
    meeting.validateLeader(userId);
    MeetingApply apply =
        meetingApplyRepositoryPort
            .findById(command.applyId())
            .filter(candidate -> candidate.meetingId().equals(meetingId))
            .orElseThrow(() -> new MeetingException(NOT_FOUND_APPLY));

    if (command.status() == MeetingApplyStatus.APPROVE) {
      meeting.validateCapacity(countApprovedApplies(meetingId));
    }
    return meetingApplyRepositoryPort.save(apply.updateStatus(command.status()));
  }

  public MeetingDetail getMeetingDetail(Long meetingId, Long userId) {
    Meeting meeting = getMeeting(meetingId);
    MeetingUser leader = getUser(meeting.userId());
    List<CoLeader> coLeaders = coLeaderRepositoryPort.findAllByMeetingId(meetingId);
    Map<Long, MeetingUser> userMap =
        getUserMap(
            coLeaders.stream()
                .map(CoLeader::userId)
                .filter(id -> !id.equals(leader.id()))
                .toList());

    List<MeetingApply> applies =
        meetingApplyRepositoryPort.findAllByMeetingIdAndStatuses(
            meetingId,
            List.of(
                MeetingApplyStatus.WAITING, MeetingApplyStatus.APPROVE, MeetingApplyStatus.REJECT));
    MeetingApplies groupedApplies = new MeetingApplies(applies);
    Map<Long, MeetingUser> applyUserMap =
        getUserMap(applies.stream().map(MeetingApply::userId).distinct().toList());
    List<ApplyDetail> applyDetails =
        applies.stream()
            .sorted(Comparator.comparing(MeetingApply::appliedDate))
            .map(apply -> new ApplyDetail(apply, applyUserMap.get(apply.userId())))
            .toList();

    return new MeetingDetail(
        meeting,
        leader,
        coLeaders.stream()
            .map(coLeader -> userMap.get(coLeader.userId()))
            .filter(user -> user != null)
            .toList(),
        meeting.isLeader(userId),
        groupedApplies.isApplied(meetingId, userId),
        groupedApplies.isApproved(meetingId, userId),
        new CoLeaders(coLeaders).isCoLeader(meetingId, userId),
        groupedApplies.getApprovedCount(meetingId),
        applyDetails);
  }

  public Page<MeetingSummary> findAllMeetings(int pageNo, int limit) {
    Pageable pageable = PageRequest.of(pageNo - 1, limit);
    Page<Meeting> meetings = meetingRepositoryPort.findAll(pageable);
    MeetingApplies applies = getMeetingApplies(meetings.getContent());
    return meetings.map(meeting -> toSummary(meeting, applies));
  }

  public Page<MeetingSummary> findMeetingsByCreator(Long userId, int pageNo, int limit) {
    Pageable pageable = PageRequest.of(pageNo - 1, limit);
    Page<Meeting> meetings = meetingRepositoryPort.findAllByUserId(userId, pageable);
    MeetingApplies applies = getMeetingApplies(meetings.getContent());
    return meetings.map(meeting -> toSummary(meeting, applies));
  }

  public MeetingPartMembers getMeetingPartMembers(Long meetingId, Long userId) {
    Meeting meeting = getMeeting(meetingId);
    MeetingUser requestUser = getUser(userId);
    Activity requestActivity = getRequestActivity(requestUser, meeting);
    boolean activeGenerationUser =
        requestUser
            .findActivityByGeneration(resolveParticipationGeneration(meeting, requestActivity))
            .isPresent();

    List<MeetingApply> participatingApplies =
        meetingApplyRepositoryPort.findAllByMeetingIdAndStatuses(
            meetingId, List.of(MeetingApplyStatus.WAITING, MeetingApplyStatus.APPROVE));
    Map<Long, MeetingUser> userMap =
        getUserMap(participatingApplies.stream().map(MeetingApply::userId).distinct().toList());

    List<ApplyDetail> appliedInfo =
        participatingApplies.stream()
            .filter(MeetingApply::isParticipating)
            .filter(
                apply ->
                    isSamePartMember(
                        userMap.get(apply.userId()), requestActivity, activeGenerationUser))
            .sorted(Comparator.comparing(MeetingApply::appliedDate))
            .map(apply -> new ApplyDetail(apply, userMap.get(apply.userId())))
            .toList();

    return new MeetingPartMembers(
        requestActivity.part() == null ? null : requestActivity.part().getName(),
        appliedInfo.size(),
        activeGenerationUser,
        requestActivity.generation(),
        appliedInfo);
  }

  private MeetingApply applyMeeting(Meeting meeting, ApplyMeetingCommand command, Long userId) {
    MeetingUser user = getUser(userId);
    CoLeaders coLeaders = new CoLeaders(coLeaderRepositoryPort.findAllByMeetingId(meeting.id()));
    List<MeetingApply> applies = meetingApplyRepositoryPort.findAllByMeetingId(meeting.id());

    validateCommonApplyRequest(meeting, user, applies, coLeaders);

    MeetingApply apply =
        MeetingApply.createApply(
            command.meetingId(), userId, command.content(), LocalDateTime.now(clock));
    return meetingApplyRepositoryPort.save(apply);
  }

  private void validateCommonApplyRequest(
      Meeting meeting, MeetingUser user, List<MeetingApply> applies, CoLeaders coLeaders) {
    meeting.validateCapacity(applies.stream().filter(MeetingApply::isApproved).count());
    if (applies.stream().anyMatch(apply -> apply.userId().equals(user.id()))) {
      throw new MeetingException(ALREADY_APPLIED_MEETING);
    }
    validateApplyPeriod(meeting);
    validateUserActivities(user);
    validateUserJoinableParts(user, meeting);
    coLeaders.validateNotCoLeader(meeting.id(), user.id());
    meeting.validateNotLeader(user.id());
  }

  private void validateApplyPeriod(Meeting meeting) {
    LocalDateTime now = LocalDateTime.now(clock);
    if (now.isBefore(meeting.startDate()) || now.isAfter(meeting.endDate())) {
      throw new MeetingException(NOT_IN_APPLY_PERIOD);
    }
  }

  private void validateUserActivities(MeetingUser user) {
    if (user.activities().isEmpty()) {
      throw new MeetingException(MISSING_USER_ACTIVITY);
    }
  }

  private void validateUserJoinableParts(MeetingUser user, Meeting meeting) {
    if (meeting.joinableParts().isEmpty()) {
      return;
    }
    List<Activity> activities = filterActivities(user, meeting);
    boolean hasJoinablePart =
        activities.stream()
            .map(activity -> toMeetingJoinablePart(activity.part()))
            .anyMatch(part -> part != null && meeting.joinableParts().contains(part));

    if (!hasJoinablePart) {
      throw new MeetingException(NOT_TARGET_PART);
    }
  }

  private List<Activity> filterActivities(MeetingUser user, Meeting meeting) {
    if (!meeting.isOnlyActiveGeneration()) {
      return user.activities();
    }
    int activeGeneration =
        meeting.targetActiveGeneration() != null
            ? meeting.targetActiveGeneration()
            : meeting.createdGeneration();
    List<Activity> filtered =
        user.activities().stream()
            .filter(activity -> activity.generation() == activeGeneration)
            .toList();
    if (filtered.isEmpty()) {
      throw new MeetingException(NOT_ACTIVE_GENERATION);
    }
    return filtered;
  }

  private boolean isSamePartMember(
      MeetingUser participatingUser, Activity requestActivity, boolean activeGenerationUser) {
    if (participatingUser == null || participatingUser.activities().isEmpty()) {
      return false;
    }
    if (!activeGenerationUser) {
      return participatingUser.activities().stream()
          .anyMatch(activity -> activity.generation() == requestActivity.generation());
    }
    MeetingJoinablePart requestPart = toMeetingJoinablePart(requestActivity.part());
    return participatingUser.activities().stream()
        .anyMatch(
            activity ->
                activity.generation() == requestActivity.generation()
                    && toMeetingJoinablePart(activity.part()) == requestPart);
  }

  private Activity getRequestActivity(MeetingUser requestUser, Meeting meeting) {
    if (requestUser.activities().isEmpty()) {
      throw new MeetingException(MISSING_USER_ACTIVITY);
    }
    int generation = resolveParticipationGeneration(meeting, null);
    return requestUser
        .findActivityByGeneration(generation)
        .orElseGet(
            () ->
                requestUser
                    .findLatestActivity()
                    .orElseThrow(() -> new MeetingException(MISSING_USER_ACTIVITY)));
  }

  private int resolveParticipationGeneration(Meeting meeting, Activity fallback) {
    if (meeting.targetActiveGeneration() != null) {
      return meeting.targetActiveGeneration();
    }
    if (meeting.createdGeneration() != null) {
      return meeting.createdGeneration();
    }
    if (fallback != null) {
      return fallback.generation();
    }
    return 0;
  }

  private MeetingJoinablePart toMeetingJoinablePart(Part part) {
    if (part == null) {
      return null;
    }
    return switch (part) {
      case PLAN, PM -> MeetingJoinablePart.PM;
      case DESIGN -> MeetingJoinablePart.DESIGN;
      case IOS -> MeetingJoinablePart.IOS;
      case ANDROID -> MeetingJoinablePart.ANDROID;
      case SERVER, BACKEND -> MeetingJoinablePart.SERVER;
      case WEB, FRONTEND -> MeetingJoinablePart.WEB;
      default -> null;
    };
  }

  private long countApprovedApplies(Long meetingId) {
    return meetingApplyRepositoryPort.countByMeetingIdAndStatus(
        meetingId, MeetingApplyStatus.APPROVE);
  }

  private MeetingApplies getMeetingApplies(List<Meeting> meetings) {
    if (meetings == null || meetings.isEmpty()) {
      return new MeetingApplies(List.of());
    }
    return new MeetingApplies(
        meetingApplyRepositoryPort.findAllByMeetingIds(
            meetings.stream().map(Meeting::id).toList()));
  }

  private MeetingSummary toSummary(Meeting meeting, MeetingApplies applies) {
    return new MeetingSummary(
        meeting,
        applies.getAppliedCount(meeting.id()),
        applies.getApprovedCount(meeting.id()),
        meeting.getMeetingStatus(LocalDateTime.now(clock)));
  }

  private int resolveCreatedGeneration(Integer commandGeneration, MeetingUser creator) {
    if (commandGeneration != null) {
      return commandGeneration;
    }
    return creator
        .findLatestActivity()
        .map(Activity::generation)
        .orElseThrow(() -> new MeetingException(MISSING_USER_ACTIVITY));
  }

  private void replaceCoLeaders(Meeting meeting, List<Long> coLeaderUserIds) {
    coLeaderRepositoryPort.deleteAllByMeetingId(meeting.id());
    if (coLeaderUserIds == null || coLeaderUserIds.isEmpty()) {
      return;
    }
    List<MeetingUser> users = meetingUserPort.findAllById(coLeaderUserIds);
    if (users.size() != coLeaderUserIds.stream().distinct().count()) {
      throw new MeetingException(NOT_FOUND_USER);
    }
    List<CoLeader> coLeaders =
        coLeaderUserIds.stream()
            .distinct()
            .map(userId -> CoLeader.create(meeting, userId))
            .toList();
    coLeaderRepositoryPort.saveAll(coLeaders);
  }

  private Meeting getMeeting(Long meetingId) {
    return meetingRepositoryPort
        .findById(meetingId)
        .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));
  }

  private MeetingUser getUser(Long userId) {
    return meetingUserPort.findById(userId).orElseThrow(() -> new MeetingException(NOT_FOUND_USER));
  }

  private Map<Long, MeetingUser> getUserMap(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }
    return meetingUserPort.findAllById(userIds.stream().distinct().toList()).stream()
        .collect(Collectors.toMap(MeetingUser::id, Function.identity()));
  }

  public record CreateMeetingCommand(
      Long meetingDemandId,
      String title,
      String subTitle,
      MeetingCategory category,
      List<MeetingImage> images,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer capacity,
      String description,
      String processDescription,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      String leaderDescription,
      String note,
      Boolean isMentorNeeded,
      Boolean canJoinOnlyActiveGeneration,
      MeetingJoinInfo joinInfo,
      Integer createdGeneration,
      Integer targetActiveGeneration,
      List<MeetingJoinablePart> joinableParts,
      List<Long> coLeaderUserIds) {}

  public record UpdateMeetingCommand(
      String title,
      String subTitle,
      MeetingCategory category,
      List<MeetingImage> images,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer capacity,
      String description,
      String processDescription,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      String leaderDescription,
      String note,
      Boolean isMentorNeeded,
      Boolean canJoinOnlyActiveGeneration,
      MeetingJoinInfo joinInfo,
      Integer targetActiveGeneration,
      List<MeetingJoinablePart> joinableParts,
      List<Long> coLeaderUserIds) {}

  public record ApplyMeetingCommand(Long meetingId, String content) {}

  public record UpdateApplyStatusCommand(Long applyId, MeetingApplyStatus status) {}

  public record MeetingSummary(
      Meeting meeting, long appliedCount, long approvedCount, MeetingStatus status) {}

  public record MeetingDetail(
      Meeting meeting,
      MeetingUser leader,
      List<MeetingUser> coLeaders,
      boolean isHost,
      boolean isApply,
      boolean isApproved,
      boolean isCoLeader,
      long approvedApplyCount,
      List<ApplyDetail> applies) {}

  public record ApplyDetail(MeetingApply apply, MeetingUser user) {}

  public record MeetingPartMembers(
      String part,
      int participantCount,
      boolean isActiveGeneration,
      Integer activeGeneration,
      List<ApplyDetail> appliedInfo) {}
}
