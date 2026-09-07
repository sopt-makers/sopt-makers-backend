package org.sopt.makers.domain.playground.community.notification;

import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.COMMENT_REPORT_TEXT_FORMAT;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.NOT_MAKERS_POST_TEXT_FORMAT;
import static org.sopt.makers.domain.playground.community.notification.CommunityNotificationConstants.POST_REPORT_TEXT_FORMAT;

/** 게시글/댓글 신고, 비 메이커스 유저 글 작성 발생 시 발송할 Slack 알림 문구를 조립한다. */
public final class CommunityReportSlackMessageFactory {

  private CommunityReportSlackMessageFactory() {}

  public static String forPostReport(Long postId, String reporterName) {
    return POST_REPORT_TEXT_FORMAT.formatted(reporterName, postId);
  }

  public static String forCommentReport(Long postId, String reporterName, String commentContent) {
    return COMMENT_REPORT_TEXT_FORMAT.formatted(reporterName, commentContent, postId);
  }

  public static String forNotMakersPost(Long postId) {
    return NOT_MAKERS_POST_TEXT_FORMAT.formatted(postId);
  }
}
