package org.sopt.makers.api.controller.crew.comment.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.playground.post.service.PostCommentService;

public record CreateCommentRequest(
    @NotNull Long postId,
    @NotBlank String contents,
    @NotNull Boolean isParent,
    Long parentCommentId) {

  @AssertTrue(message = "대댓글 작성 시 부모 댓글 id는 필수입니다.")
  public boolean isValidParentCommentId() {
    return Boolean.TRUE.equals(isParent) || parentCommentId != null;
  }

  public PostCommentService.CreateCommentCommand toCommand() {
    return new PostCommentService.CreateCommentCommand(
        contents, Boolean.TRUE.equals(isParent), parentCommentId);
  }
}
