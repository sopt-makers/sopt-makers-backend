package org.sopt.makers.domain.crew.notification.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.crew.meeting.tag.service.MeetingKeywordPreferenceService;
import org.sopt.makers.domain.crew.notification.MeetingKeywordNotificationEvent;
import org.sopt.makers.domain.crew.notification.MeetingNotification;
import org.sopt.makers.domain.crew.notification.MeetingNotificationTimePolicy;
import org.sopt.makers.domain.crew.notification.port.MeetingNotificationSenderPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingKeywordNotificationEventListener {

  private static final String TITLE_FORMAT = "💌 관심있는 키워드의 %s이 개설되었습니다!";
  private static final String CATEGORY = "NEWS";

  private final MeetingNotificationSenderPort meetingNotificationSenderPort;
  private final MeetingKeywordPreferenceService preferenceService;
  private final Clock clock;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(MeetingKeywordNotificationEvent event) {
    if (!MeetingNotificationTimePolicy.isPublishable(LocalDateTime.now(clock))) {
      return;
    }
    try {
      List<Long> userIds = preferenceService.findInterestedUserIds(event.meetingKeywordTypes());
      if (userIds.isEmpty()) {
        return;
      }
      meetingNotificationSenderPort.send(
          new MeetingNotification(
              userIds,
              TITLE_FORMAT.formatted(event.meetingCategory().getMeetingType()),
              event.meetingTitle(),
              CATEGORY,
              event.meetingId()));
    } catch (Exception e) {
      log.error("모임 키워드 알림 발송 실패. meetingId={}", event.meetingId(), e);
    }
  }
}
