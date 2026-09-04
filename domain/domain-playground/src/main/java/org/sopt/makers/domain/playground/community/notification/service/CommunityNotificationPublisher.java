package org.sopt.makers.domain.playground.community.notification.service;

import java.util.List;
import org.sopt.makers.domain.playground.community.notification.CommunityPushNotificationFactory;
import org.sopt.makers.domain.playground.community.notification.CommunityReportSlackMessageFactory;
import org.sopt.makers.domain.playground.community.notification.CommunitySlackReportEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** 커뮤니티 알림(Push/Slack) 이벤트를 발행한다. 실제 발송은 트랜잭션 커밋 이후 비동기로 처리된다. */
@Component
public class CommunityNotificationPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public CommunityNotificationPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publishPostReport(Long postId, String reporterName) {
    eventPublisher.publishEvent(
        new CommunitySlackReportEvent(CommunityReportSlackMessageFactory.forPostReport(postId, reporterName)));
  }

  public void publishCommentReport(Long postId, String reporterName, String commentContent) {
    eventPublisher.publishEvent(
        new CommunitySlackReportEvent(
            CommunityReportSlackMessageFactory.forCommentReport(postId, reporterName, commentContent)));
  }

  public void publishCommentCreated(
      Long postAuthorId, String writerName, String content, Boolean isBlindWriter, String webLink) {
    eventPublisher.publishEvent(
        CommunityPushNotificationFactory.forComment(postAuthorId, writerName, content, isBlindWriter, webLink));
  }

  public void publishReplyCreated(
      Long parentCommentAuthorId, String writerName, String content, Boolean isBlindWriter, String webLink) {
    eventPublisher.publishEvent(
        CommunityPushNotificationFactory.forReply(
            parentCommentAuthorId, writerName, content, isBlindWriter, webLink));
  }

  public void publishMention(
      List<Long> mentionedUserIds, String writerName, String content, Boolean isBlindWriter, String webLink) {
    if (mentionedUserIds == null || mentionedUserIds.isEmpty()) {
      return;
    }
    eventPublisher.publishEvent(
        CommunityPushNotificationFactory.forMention(mentionedUserIds, writerName, content, isBlindWriter, webLink));
  }
}
