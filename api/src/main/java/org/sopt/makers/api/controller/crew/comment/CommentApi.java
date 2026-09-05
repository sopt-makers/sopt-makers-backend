package org.sopt.makers.api.controller.crew.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.comment.dto.CreateCommentRequest;
import org.sopt.makers.api.controller.crew.comment.dto.GetCommentsRequest;
import org.sopt.makers.api.controller.crew.comment.dto.MentionCommentRequest;
import org.sopt.makers.api.controller.crew.comment.dto.UpdateCommentRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 게시글 댓글", description = "CREW 모임 게시글 댓글·대댓글 API")
public interface CommentApi {

  @Operation(summary = "모임 게시글 댓글·대댓글 생성")
  ResponseEntity<BaseResponse<?>> createComment(
      CreateCommentRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 댓글·대댓글 조회")
  ResponseEntity<BaseResponse<?>> getComments(
      GetCommentsRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 댓글 수정")
  ResponseEntity<BaseResponse<?>> updateComment(
      Long commentId, UpdateCommentRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 댓글 삭제")
  ResponseEntity<BaseResponse<?>> deleteComment(
      Long commentId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 댓글 신고")
  ResponseEntity<BaseResponse<?>> reportComment(
      Long commentId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 댓글 좋아요 토글")
  ResponseEntity<BaseResponse<?>> toggleCommentLike(
      Long commentId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 댓글에서 사용자 멘션")
  ResponseEntity<BaseResponse<?>> mentionUsers(
      MentionCommentRequest request, @Parameter(hidden = true) Long userId);
}
