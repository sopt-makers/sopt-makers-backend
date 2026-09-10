package org.sopt.makers.api.controller.playground.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostCommandService.PostMutationResult;

public record PostSaveResponse(
    @Schema(required = true) Long id,
    CommunityCategoryCode code,
    String title,
    String content,
    Integer hits,
    List<String> images,
    Boolean isBlindWriter,
    LocalDateTime createdAt) {

  public static PostSaveResponse from(PostMutationResult result) {
    return new PostSaveResponse(
        result.post().id(),
        result.categoryCode(),
        result.post().title(),
        result.post().content(),
        result.post().hits(),
        result.post().images(),
        result.post().isBlindWriter(),
        result.post().createdAt());
  }
}
