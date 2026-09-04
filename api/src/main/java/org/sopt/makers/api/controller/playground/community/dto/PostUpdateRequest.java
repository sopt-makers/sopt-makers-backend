package org.sopt.makers.api.controller.playground.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostCommandService.UpdatePostCommand;

public record PostUpdateRequest(
    @Schema(required = true) @NotNull Long postId,
    @Schema(required = true) @NotNull CommunityCategoryCode categoryCode,
    String title,
    String content,
    Boolean isBlindWriter,
    List<String> images,
    String link,
    MentionRequest mention) {

  public UpdatePostCommand toCommand() {
    return new UpdatePostCommand(categoryCode, title, content, isBlindWriter, images, link);
  }
}
