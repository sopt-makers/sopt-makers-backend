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
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingApplies;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.MeetingSearchCondition;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.domain.crew.meeting.Members;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;
import org.sopt.makers.domain.crew.meeting.port.MeetingApplyRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.crew.meeting.port.MemberRepositoryPort;
import org.sopt.makers.domain.user.Activity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

  private final MeetingRepositoryPort meetingRepositoryPort;
  private final MeetingApplyRepositoryPort meetingApplyRepositoryPort;
  private final MemberRepositoryPort memberRepositoryPort;
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
    memberRepositoryPort.save(Member.leader(saved.id(), userId));
    replaceCoLeaders(saved, command.coLeaderUserIds());
    return saved;
  }

  @Transactional
  public Meeting createFlashMeeting(CreateFlashMeetingCommand command, Long userId) {
    LocalDateTime now = LocalDateTime.now(clock);
    return createMeeting(
        new CreateMeetingCommand(
            null,
            command.title(),
            null,
            MeetingCategory.FLASH,
            command.images(),
            now,
            command.activityStartDate().toLocalDate().minusDays(1).atTime(23, 59, 59),
            command.maximumCapacity(),
            command.description(),
            "",
            command.activityStartDate(),
            command.activityEndDate(),
            "",
            "",
            false,
            false,
            null,
            null,
            null,
            List.of(MeetingJoinablePart.values()),
            List.of()),
        userId);
  }

  @Transactional
  public Meeting updateMeeting(Long meetingId, UpdateMeetingCommand command, Long userId) {
    Meeting meeting = getMeeting(meetingId);
    getMembers(meetingId).validateLeader(meetingId, userId);

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
  public Meeting updateFlashMeeting(
      Long meetingId, UpdateFlashMeetingCommand command, Long userId) {
    LocalDateTime now = LocalDateTime.now(clock);
    return updateMeeting(
        meetingId,
        new UpdateMeetingCommand(
            command.title(),
            null,
            MeetingCategory.FLASH,
            command.images(),
            now,
            command.activityStartDate().toLocalDate().minusDays(1).atTime(23, 59, 59),
            command.maximumCapacity(),
            command.description(),
            "",
            command.activityStartDate(),
            command.activityEndDate(),
            "",
            "",
            false,
            false,
            null,
            null,
            List.of(MeetingJoinablePart.values()),
            null),
        userId);
  }

  @Transactional
  public void deleteMeeting(Long meetingId, Long userId) {
    Meeting meeting = getMeeting(meetingId);
    getMembers(meetingId).validateLeader(meetingId, userId);
    meetingApplyRepositoryPort.deleteAllByMeetingId(meetingId);
    meetingRepositoryPort.delete(meeting);
  }

  @Transactional
  public MeetingApply applyGeneralMeeting(ApplyMeetingCommand command, Long userId) {
    Meeting meeting = getMeetingForUpdate(command.meetingId());
    if (meeting.category() == MeetingCategory.EVENT) {
      throw new MeetingException(INVALID_MEETING_CATEGORY);
    }
    return applyMeeting(meeting, command, userId);
  }

  @Transactional
  public MeetingApply applyEventMeeting(ApplyMeetingCommand command, Long userId) {
    Meeting meeting = getMeetingForUpdate(command.meetingId());
    if (meeting.category() != MeetingCategory.EVENT) {
      throw new MeetingException(INVALID_MEETING_CATEGORY);
    }
    return applyMeeting(meeting, command, userId);
  }

  @Transactional
  public void cancelApply(Long meetingId, Long userId) {
    getMeetingForUpdate(meetingId);
    if (!meetingApplyRepositoryPort.existsByMeetingIdAndUserId(meetingId, userId)) {
      throw new MeetingException(NOT_FOUND_APPLY);
    }
    meetingApplyRepositoryPort.deleteByMeetingIdAndUserId(meetingId, userId);
    memberRepositoryPort.deleteByMeetingIdAndUserIdAndRole(
        meetingId, userId, MemberRole.PARTICIPANT);
  }

  @Transactional
  public MeetingApply updateApplyStatus(
      Long meetingId, UpdateApplyStatusCommand command, Long userId) {
    Meeting meeting = getMeetingForUpdate(meetingId);
    getMembers(meetingId).validateLeader(meetingId, userId);
    MeetingApply apply =
        meetingApplyRepositoryPort
            .findById(command.applyId())
            .filter(candidate -> candidate.meetingId().equals(meetingId))
            .orElseThrow(() -> new MeetingException(NOT_FOUND_APPLY));

    if (command.status() == MeetingApplyStatus.APPROVE) {
      meeting.validateCapacity(countParticipants(meetingId));
    }
    MeetingApply updated = meetingApplyRepositoryPort.save(apply.updateStatus(command.status()));
    syncParticipant(updated);
    return updated;
  }

  public MeetingDetail getMeetingDetail(Long meetingId, Long userId) {
    Meeting meeting = getMeeting(meetingId);
    Members members = getMembers(meetingId);
    Member leaderMember = getLeaderMember(meetingId, members);
    MeetingUser leader = getUser(leaderMember.userId());
    List<Member> coLeaders = members.getByRole(MemberRole.CO_LEADER);
    Map<Long, MeetingUser> userMap =
        getUserMap(
            coLeaders.stream().map(Member::userId).filter(id -> !id.equals(leader.id())).toList());

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
        members.hasRole(meetingId, userId, MemberRole.LEADER),
        groupedApplies.isApplied(meetingId, userId),
        members.hasRole(meetingId, userId, MemberRole.PARTICIPANT),
        members.hasRole(meetingId, userId, MemberRole.CO_LEADER),
        members.getByRole(MemberRole.PARTICIPANT).size(),
        applyDetails);
  }

  public PageResult<MeetingSummary> searchMeetings(
      SearchMeetingsCommand command, int pageNo, int limit) {
    PageResult<Meeting> meetings =
        meetingRepositoryPort.search(
            new MeetingSearchCondition(
                command.search(), command.category(), command.status(), LocalDateTime.now(clock)),
            new PageQuery(pageNo, limit));
    MeetingApplies applies = getMeetingApplies(meetings.content());
    Members leaders = getMeetingMembers(meetings.content(), MemberRole.LEADER);
    Members participants = getMeetingMembers(meetings.content(), MemberRole.PARTICIPANT);
    return meetings.map(meeting -> toSummary(meeting, applies, leaders, participants));
  }

  public PageResult<MeetingSummary> findMeetingsByCreator(Long userId, int pageNo, int limit) {
    PageResult<Meeting> meetings =
        meetingRepositoryPort.findAllByLeaderUserId(userId, new PageQuery(pageNo, limit));
    MeetingApplies applies = getMeetingApplies(meetings.content());
    Members leaders = getMeetingMembers(meetings.content(), MemberRole.LEADER);
    Members participants = getMeetingMembers(meetings.content(), MemberRole.PARTICIPANT);
    return meetings.map(meeting -> toSummary(meeting, applies, leaders, participants));
  }

  public PageResult<JoinedMeeting> findJoinedMeetings(Long userId, int pageNo, int limit) {
    PageResult<Meeting> meetings =
        meetingRepositoryPort.findAllByMemberUserId(userId, new PageQuery(pageNo, limit));
    MeetingApplies applies = getMeetingApplies(meetings.content());
    Members leaders = getMeetingMembers(meetings.content(), MemberRole.LEADER);
    Members participants = getMeetingMembers(meetings.content(), MemberRole.PARTICIPANT);
    Map<Long, Member> requesterMemberMap =
        memberRepositoryPort
            .findAllByMeetingIdsAndUserId(
                meetings.content().stream().map(Meeting::id).toList(), userId)
            .stream()
            .collect(Collectors.toMap(Member::meetingId, Function.identity()));
    return meetings.map(
        meeting ->
            new JoinedMeeting(
                toSummary(meeting, applies, leaders, participants),
                getRequesterMember(meeting.id(), requesterMemberMap)));
  }

  public List<ApplyDetail> getApplicants(Long meetingId, Long userId) {
    getMeeting(meetingId);
    Members members = getMembers(meetingId);
    members.validateManager(meetingId, userId);
    return getApplyDetails(meetingApplyRepositoryPort.findAllByMeetingId(meetingId));
  }

  public List<MemberDetail> getParticipants(Long meetingId, Long userId) {
    getMeeting(meetingId);
    Members members = getMembers(meetingId);
    members.validateMember(meetingId, userId);
    List<Member> participants = members.getByRole(MemberRole.PARTICIPANT);
    Map<Long, MeetingUser> userMap =
        getUserMap(participants.stream().map(Member::userId).distinct().toList());
    return participants.stream()
        .map(participant -> new MemberDetail(participant, userMap.get(participant.userId())))
        .filter(detail -> detail.user() != null)
        .toList();
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
            meetingId, List.of(MeetingApplyStatus.APPROVE));
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
    Members members = getMembers(meeting.id());
    List<MeetingApply> applies = meetingApplyRepositoryPort.findAllByMeetingId(meeting.id());

    validateCommonApplyRequest(meeting, user, applies, members);

    MeetingApply apply =
        MeetingApply.createApply(
            command.meetingId(), userId, command.content(), LocalDateTime.now(clock));
    return meetingApplyRepositoryPort.save(apply);
  }

  private void validateCommonApplyRequest(
      Meeting meeting, MeetingUser user, List<MeetingApply> applies, Members members) {
    meeting.validateCapacity(members.getByRole(MemberRole.PARTICIPANT).size());
    if (applies.stream().anyMatch(apply -> apply.userId().equals(user.id()))) {
      throw new MeetingException(ALREADY_APPLIED_MEETING);
    }
    validateApplyPeriod(meeting);
    validateUserActivities(user);
    validateUserJoinableParts(user, meeting);
    members.validateNotCoLeader(meeting.id(), user.id());
    members.validateNotLeader(meeting.id(), user.id());
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

  private long countParticipants(Long meetingId) {
    return memberRepositoryPort.countByMeetingIdAndRole(meetingId, MemberRole.PARTICIPANT);
  }

  private MeetingApplies getMeetingApplies(List<Meeting> meetings) {
    if (meetings == null || meetings.isEmpty()) {
      return new MeetingApplies(List.of());
    }
    return new MeetingApplies(
        meetingApplyRepositoryPort.findAllByMeetingIds(
            meetings.stream().map(Meeting::id).toList()));
  }

  private Members getMeetingMembers(List<Meeting> meetings, MemberRole role) {
    if (meetings == null || meetings.isEmpty()) {
      return new Members(List.of());
    }
    return new Members(
        memberRepositoryPort.findAllByMeetingIdsAndRole(
            meetings.stream().map(Meeting::id).toList(), role));
  }

  private MeetingSummary toSummary(
      Meeting meeting, MeetingApplies applies, Members leaders, Members participants) {
    return new MeetingSummary(
        meeting,
        getLeaderMember(meeting.id(), leaders),
        applies.getAppliedCount(meeting.id()),
        participants.countByRole(meeting.id(), MemberRole.PARTICIPANT),
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
    memberRepositoryPort.deleteAllByMeetingIdAndRole(meeting.id(), MemberRole.CO_LEADER);
    if (coLeaderUserIds == null || coLeaderUserIds.isEmpty()) {
      return;
    }
    List<MeetingUser> users = meetingUserPort.findAllById(coLeaderUserIds);
    if (users.size() != coLeaderUserIds.stream().distinct().count()) {
      throw new MeetingException(NOT_FOUND_USER);
    }
    Long leaderUserId = getLeaderMember(meeting.id(), getMembers(meeting.id())).userId();
    List<Member> coLeaders =
        coLeaderUserIds.stream()
            .distinct()
            .map(userId -> Member.coLeader(meeting.id(), leaderUserId, userId))
            .toList();
    memberRepositoryPort.saveAll(coLeaders);
  }

  private void syncParticipant(MeetingApply apply) {
    apply
        .toParticipant()
        .ifPresentOrElse(
            memberRepositoryPort::save,
            () ->
                memberRepositoryPort.deleteByMeetingIdAndUserIdAndRole(
                    apply.meetingId(), apply.userId(), MemberRole.PARTICIPANT));
  }

  private Members getMembers(Long meetingId) {
    return new Members(memberRepositoryPort.findAllByMeetingId(meetingId));
  }

  private Member getLeaderMember(Long meetingId, Members members) {
    return members
        .findByRole(meetingId, MemberRole.LEADER)
        .orElseThrow(() -> new MeetingException(NOT_FOUND_USER));
  }

  private Member getRequesterMember(Long meetingId, Map<Long, Member> requesterMemberMap) {
    Member member = requesterMemberMap.get(meetingId);
    if (member == null) {
      throw new MeetingException(NOT_FOUND_USER);
    }
    return member;
  }

  private List<ApplyDetail> getApplyDetails(List<MeetingApply> applies) {
    Map<Long, MeetingUser> userMap =
        getUserMap(applies.stream().map(MeetingApply::userId).distinct().toList());
    return applies.stream()
        .sorted(Comparator.comparing(MeetingApply::appliedDate))
        .map(apply -> new ApplyDetail(apply, userMap.get(apply.userId())))
        .filter(detail -> detail.user() != null)
        .toList();
  }

  private Meeting getMeeting(Long meetingId) {
    return meetingRepositoryPort
        .findById(meetingId)
        .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));
  }

  private Meeting getMeetingForUpdate(Long meetingId) {
    return meetingRepositoryPort
        .findByIdForUpdate(meetingId)
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

  public record CreateFlashMeetingCommand(
      String title,
      String description,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      Integer maximumCapacity,
      List<MeetingImage> images) {}

  public record UpdateFlashMeetingCommand(
      String title,
      String description,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      Integer maximumCapacity,
      List<MeetingImage> images) {}

  public record UpdateApplyStatusCommand(Long applyId, MeetingApplyStatus status) {}

  public record SearchMeetingsCommand(
      String search, MeetingCategory category, MeetingStatus status) {}

  public record MeetingSummary(
      Meeting meeting,
      Member leader,
      long appliedCount,
      long approvedCount,
      MeetingStatus status) {}

  public record JoinedMeeting(MeetingSummary summary, Member member) {}

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

  public record MemberDetail(Member member, MeetingUser user) {}

  public record MeetingPartMembers(
      String part,
      int participantCount,
      boolean isActiveGeneration,
      Integer activeGeneration,
      List<ApplyDetail> appliedInfo) {}
}
