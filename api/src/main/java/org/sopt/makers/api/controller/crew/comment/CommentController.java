package org.sopt.makers.api.controller.crew.comment;

import static org.sopt.makers.api.controller.crew.comment.CommentSuccessCode.CREATE_COMMENT;
import static org.sopt.makers.api.controller.crew.comment.CommentSuccessCode.DELETE_COMMENT;
import static org.sopt.makers.api.controller.crew.comment.CommentSuccessCode.GET_COMMENTS;
import static org.sopt.makers.api.controller.crew.comment.CommentSuccessCode.MENTION_COMMENT_USERS;
import static org.sopt.makers.api.controller.crew.comment.CommentSuccessCode.REPORT_COMMENT;
import static org.sopt.makers.api.controller.crew.comment.CommentSuccessCode.TOGGLE_COMMENT_LIKE;
import static org.sopt.makers.api.controller.crew.comment.CommentSuccessCode.UPDATE_COMMENT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.comment.dto.CommentPageResponse;
import org.sopt.makers.api.controller.crew.comment.dto.CreateCommentRequest;
import org.sopt.makers.api.controller.crew.comment.dto.CreateCommentResponse;
import org.sopt.makers.api.controller.crew.comment.dto.GetCommentsRequest;
import org.sopt.makers.api.controller.crew.comment.dto.MentionCommentRequest;
import org.sopt.makers.api.controller.crew.comment.dto.ReportCommentResponse;
import org.sopt.makers.api.controller.crew.comment.dto.ToggleCommentLikeResponse;
import org.sopt.makers.api.controller.crew.comment.dto.UpdateCommentRequest;
import org.sopt.makers.api.controller.crew.comment.dto.UpdateCommentResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.post.comment.PostComment;
import org.sopt.makers.domain.playground.post.report.PostCommentReport;
import org.sopt.makers.domain.playground.post.service.PostCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment/v2")
@RequiredArgsConstructor
public class CommentController implements CommentApi {

  private final PostCommentService commentService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createComment(
      @Valid @RequestBody CreateCommentRequest request, @CurrentUserId Long userId) {
    PostComment comment =
        commentService.createComment(request.postId(), request.toCommand(), userId);
    return ResponseFactory.success(CREATE_COMMENT, new CreateCommentResponse(comment.id()));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getComments(
      @Valid @ModelAttribute GetCommentsRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_COMMENTS,
        CommentPageResponse.from(
            commentService.findComments(
                request.postId(), userId, request.pageOrDefault(), request.takeOrDefault())));
  }

  @Override
  @PutMapping("/{commentId}")
  public ResponseEntity<BaseResponse<?>> updateComment(
      @PathVariable Long commentId,
      @Valid @RequestBody UpdateCommentRequest request,
      @CurrentUserId Long userId) {
    PostCommentService.UpdatedComment updated =
        commentService.updateComment(commentId, request.contents(), userId);
    return ResponseFactory.success(
        UPDATE_COMMENT,
        new UpdateCommentResponse(
            updated.comment().id(),
            updated.comment().contents(),
            String.valueOf(updated.updatedAt())));
  }

  @Override
  @DeleteMapping("/{commentId}")
  public ResponseEntity<BaseResponse<?>> deleteComment(
      @PathVariable Long commentId, @CurrentUserId Long userId) {
    commentService.deleteComment(commentId, userId);
    return ResponseFactory.success(DELETE_COMMENT);
  }

  @Override
  @PostMapping("/{commentId}/report")
  public ResponseEntity<BaseResponse<?>> reportComment(
      @PathVariable Long commentId, @CurrentUserId Long userId) {
    PostCommentReport report = commentService.reportComment(commentId, userId);
    return ResponseFactory.success(REPORT_COMMENT, new ReportCommentResponse(report.id()));
  }

  @Override
  @PostMapping("/{commentId}/like")
  public ResponseEntity<BaseResponse<?>> toggleCommentLike(
      @PathVariable Long commentId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        TOGGLE_COMMENT_LIKE,
        new ToggleCommentLikeResponse(commentService.toggleCommentLike(commentId, userId)));
  }

  @Override
  @PostMapping("/mention")
  public ResponseEntity<BaseResponse<?>> mentionUsers(
      @Valid @RequestBody MentionCommentRequest request, @CurrentUserId Long userId) {
    commentService.mentionUsers(request.postId(), request.orgIds(), request.content(), userId);
    return ResponseFactory.success(MENTION_COMMENT_USERS);
  }
}
