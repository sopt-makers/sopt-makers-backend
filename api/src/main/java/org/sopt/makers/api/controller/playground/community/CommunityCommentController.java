package org.sopt.makers.api.controller.playground.community;

import static org.sopt.makers.api.controller.playground.community.CommunityCommentSuccessCode.CREATE_COMMENT;
import static org.sopt.makers.api.controller.playground.community.CommunityCommentSuccessCode.DELETE_COMMENT;
import static org.sopt.makers.api.controller.playground.community.CommunityCommentSuccessCode.GET_COMMENTS;
import static org.sopt.makers.api.controller.playground.community.CommunityCommentSuccessCode.LIKE_COMMENT;
import static org.sopt.makers.api.controller.playground.community.CommunityCommentSuccessCode.REPORT_COMMENT;
import static org.sopt.makers.api.controller.playground.community.CommunityCommentSuccessCode.UNLIKE_COMMENT;
import static org.sopt.makers.api.controller.playground.community.CommunityCommentSuccessCode.UPDATE_COMMENT;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.community.dto.CommentResponse;
import org.sopt.makers.api.controller.playground.community.dto.CommentSaveRequest;
import org.sopt.makers.api.controller.playground.community.dto.CommentUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.community.comment.CommentThread;
import org.sopt.makers.domain.playground.community.comment.service.CommentCommandService;
import org.sopt.makers.domain.playground.community.comment.service.CommentQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("playgroundCommunityCommentController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/{postId}/comment")
public class CommunityCommentController implements CommunityCommentApi {

  private final CommentQueryService commentQueryService;
  private final CommentCommandService commentCommandService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createComment(
      @PathVariable("postId") Long postId, @CurrentUserId Long userId, @RequestBody @Valid CommentSaveRequest request) {
    commentCommandService.createComment(userId, postId, request.toCommand());
    return ResponseFactory.success(CREATE_COMMENT);
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getComments(
      @CurrentUserId Long userId,
      @PathVariable("postId") Long postId,
      @RequestParam(value = "isBlockOn", required = false, defaultValue = "true") Boolean isBlockOn) {
    List<CommentThread> threads = commentQueryService.getCommentThreadsByPostId(userId, postId, isBlockOn);
    List<CommentResponse> flatComments = threads.stream().map(CommentResponse::from).toList();
    List<CommentResponse> hierarchicalComments = CommentResponse.buildHierarchy(flatComments);
    return ResponseFactory.success(GET_COMMENTS, hierarchicalComments);
  }

  @Override
  @PatchMapping("/{commentId}")
  public ResponseEntity<BaseResponse<?>> updateComment(
      @PathVariable("commentId") Long commentId,
      @CurrentUserId Long userId,
      @RequestBody @Valid CommentUpdateRequest request) {
    commentCommandService.updateComment(userId, commentId, request.content());
    return ResponseFactory.success(UPDATE_COMMENT);
  }

  @Override
  @DeleteMapping("/{commentId}")
  public ResponseEntity<BaseResponse<?>> deleteComment(
      @PathVariable("commentId") Long commentId, @CurrentUserId Long userId) {
    commentCommandService.deleteComment(userId, commentId);
    return ResponseFactory.success(DELETE_COMMENT);
  }

  @Override
  @PostMapping("/{commentId}/report")
  public ResponseEntity<BaseResponse<?>> reportComment(
      @PathVariable("commentId") Long commentId, @CurrentUserId Long userId) {
    commentCommandService.reportComment(userId, commentId);
    return ResponseFactory.success(REPORT_COMMENT);
  }

  @Override
  @PostMapping("/{commentId}/like")
  public ResponseEntity<BaseResponse<?>> likeComment(
      @PathVariable("postId") Long postId,
      @PathVariable("commentId") Long commentId,
      @CurrentUserId Long userId) {
    commentCommandService.likeComment(userId, postId, commentId);
    return ResponseFactory.success(LIKE_COMMENT);
  }

  @Override
  @DeleteMapping("/{commentId}/unlike")
  public ResponseEntity<BaseResponse<?>> unlikeComment(
      @PathVariable("postId") Long postId,
      @PathVariable("commentId") Long commentId,
      @CurrentUserId Long userId) {
    commentCommandService.unlikeComment(userId, postId, commentId);
    return ResponseFactory.success(UNLIKE_COMMENT);
  }
}
