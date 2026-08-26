package org.sopt.makers.domain.crew.meeting.demand.service;

import static org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReportTarget.COMMENT;
import static org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReportTarget.DEMAND;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.ALREADY_REPORTED_MEETING_DEMAND;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.INVALID_MEETING_DEMAND_VALUE;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.NOT_FOUND_MEETING_DEMAND;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.NOT_FOUND_MEETING_DEMAND_USER;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.WRITER_CANNOT_REPORT_MEETING_DEMAND;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandJoinInfo;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReport;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandWait;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandWaitHistory;
import org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandException;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentLikeRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentProfileRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandReportRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandWaitHistoryRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandWaitRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandService {

  private static final String NOTIFICATION_CATEGORY = "NEWS";
  private static final String WAIT_NOTIFICATION_TITLE = "내가 제안한 모임을 기다려요!";
  private static final String WAIT_NOTIFICATION_CONTENT = "내 제안에 관심을 보인 멤버가 있어요.";

  private final MeetingDemandRepositoryPort meetingDemandRepositoryPort;
  private final MeetingDemandWaitRepositoryPort waitRepositoryPort;
  private final MeetingDemandWaitHistoryRepositoryPort waitHistoryRepositoryPort;
  private final MeetingDemandCommentRepositoryPort commentRepositoryPort;
  private final MeetingDemandCommentLikeRepositoryPort commentLikeRepositoryPort;
  private final MeetingDemandCommentProfileRepositoryPort commentProfileRepositoryPort;
  private final MeetingDemandReportRepositoryPort reportRepositoryPort;
  private final MeetingRepositoryPort meetingRepositoryPort;
  private final MeetingUserPort meetingUserPort;
  private final MeetingDemandNotificationPublisher notificationPublisher;

  public Page<MeetingDemandSummary> findMeetingDemands(Long userId, int page, int limit) {
    Page<MeetingDemand> demands = findNormalizedDemandPage(page, limit);
    List<Long> demandIds = demands.getContent().stream().map(MeetingDemand::id).toList();
    Set<Long> waitingDemandIds =
        waitRepositoryPort.findMeetingDemandIdsByUserIdAndMeetingDemandIds(userId, demandIds);
    return demands.map(
        demand ->
            new MeetingDemandSummary(
                demand, demand.isWriter(userId), waitingDemandIds.contains(demand.id())));
  }

  public MeetingDemandDetail getMeetingDemand(Long meetingDemandId, Long userId) {
    MeetingDemand demand = getDemand(meetingDemandId);
    return new MeetingDemandDetail(
        demand,
        demand.isWriter(userId),
        waitRepositoryPort.existsByMeetingDemandIdAndUserId(meetingDemandId, userId),
        Math.toIntExact(meetingRepositoryPort.countByMeetingDemandId(meetingDemandId)));
  }

  public Page<OpenedMeeting> findOpenedMeetings(Long meetingDemandId, int page, int limit) {
    validateExists(meetingDemandId);
    Page<Meeting> meetings = findNormalizedOpenedMeetingPage(meetingDemandId, page, limit);
    Map<Long, MeetingUser> userMap = getUserMap(meetings.getContent());
    return meetings.map(meeting -> new OpenedMeeting(meeting, userMap.get(meeting.userId())));
  }

  @Transactional
  public MeetingDemand createMeetingDemand(CreateMeetingDemandCommand command, Long userId) {
    validateUser(userId);
    validateCreateCommand(command);
    return meetingDemandRepositoryPort.save(
        MeetingDemand.create(
            userId,
            command.shortIntro(),
            command.expectation(),
            command.meetingKeywordTypes(),
            command.joinInfo()));
  }

  @Transactional
  public void deleteMeetingDemand(Long meetingDemandId, Long userId) {
    MeetingDemand demand = getDemandForUpdate(meetingDemandId);
    demand.validateWriter(userId);

    List<Long> commentIds = commentRepositoryPort.findAllIdsByMeetingDemandId(meetingDemandId);
    commentLikeRepositoryPort.deleteAllByCommentIds(commentIds);
    commentIds.forEach(commentId -> reportRepositoryPort.deleteAllByTarget(COMMENT, commentId));
    commentRepositoryPort.deleteAllByMeetingDemandId(meetingDemandId);
    commentProfileRepositoryPort.deleteAllByMeetingDemandId(meetingDemandId);
    waitRepositoryPort.deleteAllByMeetingDemandId(meetingDemandId);
    waitHistoryRepositoryPort.deleteAllByMeetingDemandId(meetingDemandId);
    reportRepositoryPort.deleteAllByTarget(DEMAND, meetingDemandId);
    meetingRepositoryPort.clearMeetingDemandId(meetingDemandId);
    meetingDemandRepositoryPort.delete(demand);
  }

  @Transactional
  public WaitResult toggleWait(Long meetingDemandId, Long userId) {
    MeetingDemand demand = getDemandForUpdate(meetingDemandId);
    demand.validateNotWriter(userId);
    validateUser(userId);

    boolean wasWaiting =
        waitRepositoryPort.existsByMeetingDemandIdAndUserId(meetingDemandId, userId);
    if (wasWaiting) {
      waitRepositoryPort.deleteByMeetingDemandIdAndUserId(meetingDemandId, userId);
    } else {
      waitRepositoryPort.save(MeetingDemandWait.create(meetingDemandId, userId));
      publishFirstWaitNotification(demand, userId);
    }

    long waitCount = waitRepositoryPort.countByMeetingDemandId(meetingDemandId);
    meetingDemandRepositoryPort.save(demand.syncWaitCount(waitCount));
    return new WaitResult(Math.toIntExact(waitCount), !wasWaiting);
  }

  @Transactional
  public MeetingDemandReport reportMeetingDemand(Long meetingDemandId, Long userId) {
    MeetingDemand demand = getDemand(meetingDemandId);
    if (demand.isWriter(userId)) {
      throw new MeetingDemandException(WRITER_CANNOT_REPORT_MEETING_DEMAND);
    }
    if (reportRepositoryPort.existsByUserIdAndTarget(userId, DEMAND, meetingDemandId)) {
      throw new MeetingDemandException(ALREADY_REPORTED_MEETING_DEMAND);
    }
    return reportRepositoryPort.save(MeetingDemandReport.demand(userId, meetingDemandId));
  }

  @Transactional
  public MeetingDemand open(Long meetingDemandId) {
    return meetingDemandRepositoryPort.save(getDemandForUpdate(meetingDemandId).open());
  }

  public void validateExists(Long meetingDemandId) {
    if (meetingDemandId != null) {
      getDemand(meetingDemandId);
    }
  }

  public MeetingDemand getDemand(Long meetingDemandId) {
    return meetingDemandRepositoryPort
        .findById(meetingDemandId)
        .orElseThrow(() -> new MeetingDemandException(NOT_FOUND_MEETING_DEMAND));
  }

  public MeetingDemand getDemandForUpdate(Long meetingDemandId) {
    return meetingDemandRepositoryPort
        .findByIdForUpdate(meetingDemandId)
        .orElseThrow(() -> new MeetingDemandException(NOT_FOUND_MEETING_DEMAND));
  }

  private void validateUser(Long userId) {
    if (meetingUserPort.findById(userId).isEmpty()) {
      throw new MeetingDemandException(NOT_FOUND_MEETING_DEMAND_USER);
    }
  }

  private void validateCreateCommand(CreateMeetingDemandCommand command) {
    if (command.shortIntro() == null
        || command.shortIntro().isBlank()
        || command.expectation() == null
        || command.expectation().isBlank()
        || command.meetingKeywordTypes() == null
        || command.meetingKeywordTypes().isEmpty()
        || command.meetingKeywordTypes().size() > 2) {
      throw new MeetingDemandException(INVALID_MEETING_DEMAND_VALUE);
    }
  }

  private void publishFirstWaitNotification(MeetingDemand demand, Long waitingUserId) {
    if (waitHistoryRepositoryPort.existsByMeetingDemandIdAndUserId(demand.id(), waitingUserId)) {
      return;
    }
    waitHistoryRepositoryPort.save(MeetingDemandWaitHistory.create(demand.id(), waitingUserId));
    notificationPublisher.publish(
        new MeetingDemandNotification(
            List.of(demand.userId()),
            WAIT_NOTIFICATION_TITLE,
            WAIT_NOTIFICATION_CONTENT,
            NOTIFICATION_CATEGORY,
            "/suggest/detail?id=" + demand.id()));
  }

  private Page<MeetingDemand> findNormalizedDemandPage(int page, int limit) {
    Pageable pageable = demandPageable(page, limit);
    Page<MeetingDemand> result = meetingDemandRepositoryPort.findAll(pageable);
    if (result.getTotalPages() > 0 && page > result.getTotalPages()) {
      return meetingDemandRepositoryPort.findAll(demandPageable(result.getTotalPages(), limit));
    }
    return result;
  }

  private Page<Meeting> findNormalizedOpenedMeetingPage(Long meetingDemandId, int page, int limit) {
    Pageable pageable = demandPageable(page, limit);
    Page<Meeting> result =
        meetingRepositoryPort.findAllByMeetingDemandId(meetingDemandId, pageable);
    if (result.getTotalPages() > 0 && page > result.getTotalPages()) {
      return meetingRepositoryPort.findAllByMeetingDemandId(
          meetingDemandId, demandPageable(result.getTotalPages(), limit));
    }
    return result;
  }

  private Pageable demandPageable(int page, int limit) {
    return PageRequest.of(
        Math.max(0, page - 1), limit, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
  }

  private Map<Long, MeetingUser> getUserMap(List<Meeting> meetings) {
    List<Long> userIds = meetings.stream().map(Meeting::userId).distinct().toList();
    if (userIds.isEmpty()) {
      return Map.of();
    }
    return meetingUserPort.findAllById(userIds).stream()
        .collect(
            Collectors.toMap(
                MeetingUser::id, Function.identity(), (left, right) -> left, LinkedHashMap::new));
  }

  public record CreateMeetingDemandCommand(
      String shortIntro,
      String expectation,
      List<MeetingKeywordType> meetingKeywordTypes,
      MeetingDemandJoinInfo joinInfo) {}

  public record MeetingDemandSummary(
      MeetingDemand meetingDemand, boolean isMine, boolean isWaiting) {}

  public record MeetingDemandDetail(
      MeetingDemand meetingDemand, boolean isMine, boolean isWaiting, int openedMeetingCount) {}

  public record OpenedMeeting(Meeting meeting, MeetingUser creator) {}

  public record WaitResult(int waitCount, boolean isWaiting) {}
}
