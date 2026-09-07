package org.sopt.makers.domain.playground.community.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.community.notification.CommunityNotMakersPostEvent;
import org.sopt.makers.domain.playground.community.notification.CommunityPushNotification;
import org.sopt.makers.domain.playground.community.notification.CommunitySlackReportEvent;
import org.sopt.makers.domain.playground.community.notification.port.CommunityPushNotificationPort;
import org.sopt.makers.domain.playground.community.notification.port.CommunitySlackNotificationPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 커뮤니티 Push/Slack 알림 사이드 이펙트를 처리한다. 게시글/댓글 저장 트랜잭션이 커밋된 이후 별도 스레드에서 비동기로 발송하며, 발송 실패가 핵심
 * 비즈니스 트랜잭션에 영향을 주지 않도록 예외를 여기서 격리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommunityNotificationEventListener {

  private final CommunityPushNotificationPort communityPushNotificationPort;
  private final CommunitySlackNotificationPort communitySlackNotificationPort;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePushNotification(CommunityPushNotification notification) {
    try {
      communityPushNotificationPort.send(notification);
    } catch (Exception exception) {
      log.error("커뮤니티 푸시 알림 발송 실패. recipientIds={}", notification.recipientIds(), exception);
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSlackReport(CommunitySlackReportEvent event) {
    try {
      communitySlackNotificationPort.sendReportMessage(event.message());
    } catch (Exception exception) {
      log.error("커뮤니티 신고 슬랙 알림 발송 실패.", exception);
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleNotMakersPost(CommunityNotMakersPostEvent event) {
    try {
      communitySlackNotificationPort.sendNotMakersMessage(event.message());
    } catch (Exception exception) {
      log.error("비 메이커스 게시글 작성 슬랙 알림 발송 실패.", exception);
    }
  }
}
