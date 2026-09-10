package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.api.common.util.RelativeTimeFormatter;
import org.sopt.makers.domain.playground.community.CommunityPostTag;
import org.sopt.makers.domain.playground.community.post.RecentPost;

public record RecentPostResponse(
    Long id,
    String title,
    String content,
    String createdAt,
    int likeCount,
    int commentCount,
    CommunityPostTag categoryTag,
    String categoryTagLabel,
    Integer totalVoteCount) {

  public static RecentPostResponse from(RecentPost post) {
    return new RecentPostResponse(
        post.id(),
        post.title(),
        post.content(),
        RelativeTimeFormatter.format(post.createdAt()),
        post.likeCount(),
        post.commentCount(),
        post.categoryTag(),
        post.categoryTag() == null ? null : post.categoryTag().getLabel(),
        post.totalVoteCount());
  }
}
