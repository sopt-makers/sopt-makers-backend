package org.sopt.makers.domain.playground.community.notification;

import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.COMMENT_CONTENT_FORMAT;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.COMMENT_NOTIFICATION_TITLE;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.CONTENT_MAX_LENGTH;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.MENTION_CONTENT_FORMAT;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.MENTION_NOTIFICATION_TITLE_FORMAT;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.REPLY_CONTENT_FORMAT;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.REPLY_NOTIFICATION_TITLE;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.WRITER_ANONYMOUS;

import java.util.List;
import org.sopt.makers.domain.playground.community.utils.MentionCleaner;

/** 댓글 작성/답글/멘션 발생 시 발송할 커뮤니티 푸시 알림 문구를 조립한다. */
public final class CommunityPushNotificationFactory {

  private CommunityPushNotificationFactory() {}

  public static CommunityPushNotification forComment(
      Long postAuthorId, String writerName, String content, Boolean isBlindWriter, String webLink) {
    return new CommunityPushNotification(
        List.of(postAuthorId),
        COMMENT_NOTIFICATION_TITLE,
        formatContent(COMMENT_CONTENT_FORMAT, writerName, content, isBlindWriter),
        webLink);
  }

  public static CommunityPushNotification forReply(
      Long parentCommentAuthorId, String writerName, String content, Boolean isBlindWriter, String webLink) {
    return new CommunityPushNotification(
        List.of(parentCommentAuthorId),
        REPLY_NOTIFICATION_TITLE,
        formatContent(REPLY_CONTENT_FORMAT, writerName, content, isBlindWriter),
        webLink);
  }

  public static CommunityPushNotification forMention(
      List<Long> mentionedUserIds, String writerName, String content, Boolean isBlindWriter, String webLink) {
    String displayName = Boolean.TRUE.equals(isBlindWriter) ? WRITER_ANONYMOUS : writerName;
    return new CommunityPushNotification(
        mentionedUserIds,
        MENTION_NOTIFICATION_TITLE_FORMAT.formatted(displayName),
        MENTION_CONTENT_FORMAT.formatted(abbreviate(MentionCleaner.removeMentionIds(content))),
        webLink);
  }

  private static String formatContent(String format, String writerName, String content, Boolean isBlindWriter) {
    String displayName = Boolean.TRUE.equals(isBlindWriter) ? WRITER_ANONYMOUS : writerName;
    return format.formatted(displayName, abbreviate(MentionCleaner.removeMentionIds(content)));
  }

  private static String abbreviate(String content) {
    if (content == null || content.length() <= CONTENT_MAX_LENGTH) {
      return content;
    }
    return content.substring(0, CONTENT_MAX_LENGTH - 3) + "...";
  }
}
