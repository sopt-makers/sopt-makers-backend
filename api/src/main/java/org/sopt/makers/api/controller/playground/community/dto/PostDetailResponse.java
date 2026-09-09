package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.domain.playground.community.post.PostDetail;

public record PostDetailResponse(
    MemberResponse member,
    PostDetailPostResponse posts,
    PostDetailCategoryResponse category,
    Boolean isMine,
    Boolean isLiked,
    Integer likes,
    AnonymousProfileResponse anonymousProfile) {

  public static PostDetailResponse from(PostDetail detail) {
    boolean isBlindWriter = Boolean.TRUE.equals(detail.post().isBlindWriter());

    return new PostDetailResponse(
        isBlindWriter ? null : MemberResponse.from(detail.member()),
        PostDetailPostResponse.from(detail),
        PostDetailCategoryResponse.from(detail.category(), detail.parentCategory()),
        detail.isMine(),
        detail.isLiked(),
        detail.likes(),
        isBlindWriter ? AnonymousProfileResponse.from(detail.anonymousProfile()) : null);
  }
}
