package org.sopt.makers.domain.crew.meeting.demand.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandOpenedNotification;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandOpenedNotificationRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandWaitRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingDemandOpenedNotificationService {

  private static final String NOTIFICATION_CATEGORY = "NEWS";
  private static final String OPENED_NOTIFICATION_TITLE = "기다리던 모임이 열렸어요";
  private static final String OPENED_NOTIFICATION_CONTENT = "관심을 보였던 수요가 모임으로 개설됐어요.";

  private final MeetingDemandOpenedNotificationRepositoryPort notificationRepositoryPort;
  private final MeetingDemandWaitRepositoryPort waitRepositoryPort;
  private final MeetingDemandCommentRepositoryPort commentRepositoryPort;
  private final MeetingRepositoryPort meetingRepositoryPort;
  private final MeetingDemandService meetingDemandService;
  private final MeetingDemandNotificationPublisher notificationPublisher;
  private final Clock clock;

  @Transactional
  public void register(Meeting meeting) {
    if (meeting.meetingDemandId() == null) {
      return;
    }
    meetingDemandService.validateExists(meeting.meetingDemandId());
    MeetingStatus status = meeting.getMeetingStatus(LocalDateTime.now(clock));
    if (status == MeetingStatus.RECRUITMENT_COMPLETE) {
      return;
    }

    MeetingDemandOpenedNotification notification =
        notificationRepositoryPort
            .findByMeetingId(meeting.id())
            .orElseGet(
                () ->
                    notificationRepositoryPort.save(
                        MeetingDemandOpenedNotification.create(meeting.id())));
    if (!notification.isSent() && status == MeetingStatus.APPLY_ABLE) {
      publishOpenedNotification(meeting, notification);
    }
  }

  @Transactional
  public void sendNotification(Long meetingId) {
    Optional<MeetingDemandOpenedNotification> notificationOptional =
        notificationRepositoryPort.findByMeetingId(meetingId);
    if (notificationOptional.isEmpty() || notificationOptional.get().isSent()) {
      return;
    }

    Optional<Meeting> meetingOptional = meetingRepositoryPort.findById(meetingId);
    if (meetingOptional.isEmpty()) {
      return;
    }
    Meeting meeting = meetingOptional.get();
    if (meeting.meetingDemandId() == null
        || meeting.getMeetingStatus(LocalDateTime.now(clock)) != MeetingStatus.APPLY_ABLE) {
      return;
    }
    publishOpenedNotification(meeting, notificationOptional.get());
  }

  @Transactional
  public void sendPendingNotifications() {
    notificationRepositoryPort
        .findPendingMeetingIds(LocalDateTime.now(clock))
        .forEach(this::sendNotification);
  }

  @Transactional
  public void markSent(Long notificationId) {
    notificationRepositoryPort
        .findById(notificationId)
        .filter(notification -> !notification.isSent())
        .map(notification -> notification.markSent(LocalDateTime.now(clock)))
        .ifPresent(notificationRepositoryPort::save);
  }

  private void publishOpenedNotification(
      Meeting meeting, MeetingDemandOpenedNotification openedNotification) {
    MeetingDemand demand = meetingDemandService.getDemand(meeting.meetingDemandId());
    Set<Long> receiverIds = new LinkedHashSet<>();
    receiverIds.add(demand.userId());
    receiverIds.addAll(
        commentRepositoryPort.findDistinctWriterUserIdsByMeetingDemandId(demand.id()));
    receiverIds.addAll(waitRepositoryPort.findUserIdsByMeetingDemandId(demand.id()));
    receiverIds.remove(null);
    receiverIds.remove(meeting.userId());
    if (receiverIds.isEmpty()) {
      notificationRepositoryPort.save(openedNotification.markSent(LocalDateTime.now(clock)));
      return;
    }

    notificationPublisher.publishOpened(
        new MeetingDemandNotification(
            List.copyOf(receiverIds),
            OPENED_NOTIFICATION_TITLE,
            OPENED_NOTIFICATION_CONTENT,
            NOTIFICATION_CATEGORY,
            "/detail?id=" + meeting.id()),
        openedNotification.id());
  }
}
