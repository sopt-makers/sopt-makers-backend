package org.sopt.makers.api.controller.playground.community.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.domain.playground.community.post.PostDetail;

/** vote는 vote 도메인이 아직 이관되지 않아 항상 null이다(TODO). */
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
        null);
  }
}
