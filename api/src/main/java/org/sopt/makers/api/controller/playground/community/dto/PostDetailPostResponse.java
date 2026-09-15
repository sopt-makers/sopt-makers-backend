package org.sopt.makers.api.controller.playground.community.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.domain.playground.community.post.PostDetail;

public record PostDetailPostResponse(
    Long id,
    CommunityCategoryGroup categoryGroup,
    CommunityCategoryCode categoryCode,
    String title,
    String content,
    Integer hits,
    List<String> images,
    Boolean isBlindWriter,
    String sopticleUrl,
    Boolean isReported,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Object vote) {

  public static PostDetailPostResponse from(PostDetail detail) {
    Post post = detail.post();
    Category category = detail.category();

    return new PostDetailPostResponse(
        post.id(),
        category == null ? null : category.categoryGroup(),
        category == null ? null : category.code(),
        post.title(),
        post.content(),
        post.hits(),
        post.images(),
        post.isBlindWriter(),
        post.sopticleUrl(),
        post.isReported(),
        post.createdAt(),
        post.updatedAt(),
        detail.vote() == null ? null : VoteResponse.from(detail.vote()));
  }
}
