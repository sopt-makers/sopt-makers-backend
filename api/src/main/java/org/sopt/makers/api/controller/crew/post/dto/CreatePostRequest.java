package org.sopt.makers.api.controller.crew.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.domain.playground.post.service.PostService;

public record CreatePostRequest(
    @NotNull Long meetingId,
    @NotBlank String title,
    @Size(max = 10) List<String> images,
    @NotBlank String contents,
    PostContentType postCategory) {

  public PostService.CreateMeetingPostCommand toCommand() {
    return new PostService.CreateMeetingPostCommand(
        meetingId, title, contents, images, postCategory);
  }
}
