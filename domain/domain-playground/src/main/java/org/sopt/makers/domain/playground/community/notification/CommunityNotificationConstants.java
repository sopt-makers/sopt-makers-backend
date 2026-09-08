package org.sopt.makers.domain.playground.community.notification;

final class CommunityNotificationConstants {

  private CommunityNotificationConstants() {}

  static final String COMMENT_NOTIFICATION_TITLE = "💬나의 게시글에 새로운 댓글이 달렸어요.";
  static final String REPLY_NOTIFICATION_TITLE = "💬나의 댓글에 새로운 답글이 달렸어요.";
  static final String MENTION_NOTIFICATION_TITLE_FORMAT = "💬%s님이 회원님을 언급했어요.";
  static final String COMMENT_CONTENT_FORMAT = "[%s의 댓글] : \"%s\"";
  static final String REPLY_CONTENT_FORMAT = "[%s의 답글] : \"%s\"";
  static final String MENTION_CONTENT_FORMAT = "\"%s\"";
  static final String WRITER_ANONYMOUS = "익명";
  static final int CONTENT_MAX_LENGTH = 100;

  static final String POST_REPORT_TEXT_FORMAT =
      "🚨글 신고 발생!🚨\n*신고자:*\n%s\n*글 링크:*\n<https://playground.sopt.org/feed/%d|글>";
  static final String COMMENT_REPORT_TEXT_FORMAT =
      "🚨댓글 신고 발생!🚨\n*신고자:*\n%s\n*댓글 내용:*\n%s\n*링크:*\n<https://playground.sopt.org/feed/%d|글>";
  static final String NOT_MAKERS_POST_TEXT_FORMAT =
      "💙 비 메이커스 유저 글 작성\n비 메이커스 유저가 글을 작성했어요!\n*글 링크:*\n<https://playground.sopt.org/feed/%d|링크>";
}
