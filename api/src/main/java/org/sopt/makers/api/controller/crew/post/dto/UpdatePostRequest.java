package org.sopt.makers.api.controller.crew.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.domain.playground.post.service.PostService;

public record UpdatePostRequest(
    @NotBlank String title, @NotBlank String contents, @Size(max = 10) List<String> images) {

  public PostService.UpdatePostCommand toCommand() {
    return new PostService.UpdatePostCommand(title, contents, images);
  }
}
