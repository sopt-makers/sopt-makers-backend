package org.sopt.makers.api.controller.playground.resolution.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.sopt.makers.domain.playground.resolution.ResolutionTag;

public record ResolutionSaveRequest(
    List<String> tags,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Content cannot be empty or blank.")
        String content) {
  public List<ResolutionTag> toTags() {
    return ResolutionTag.fromStringList(tags);
  }
}
