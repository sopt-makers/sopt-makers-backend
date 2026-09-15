package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.domain.playground.community.CommunityPostTag;
import org.sopt.makers.domain.playground.community.post.PopularPost;

public record PopularPostResponse(
    Long id,
    String title,
    MemberSummaryResponse member,
    Integer hits,
    int likeCount,
    int commentCount,
    CommunityPostTag categoryTag,
    String categoryTagLabel) {

  public static PopularPostResponse from(PopularPost post) {
    return new PopularPostResponse(
        post.id(),
        post.title(),
        MemberSummaryResponse.from(post.member()),
        post.hits(),
        post.likeCount(),
        post.commentCount(),
        post.categoryTag(),
        post.categoryTag() == null ? null : post.categoryTag().getLabel());
  }
}
