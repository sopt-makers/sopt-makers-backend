package org.sopt.makers.api.controller.playground.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.playground.community.dto.CommentSaveRequest;
import org.sopt.makers.api.controller.playground.community.dto.CommentUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Community Comment 관련 API", description = "커뮤니티 댓글 관련 API")
@SecurityRequirement(name = "Authorization")
public interface CommunityCommentApi {

  @Operation(
      summary = "댓글/답글 생성 API",
      description =
          """
          댓글 또는 답글을 생성합니다.
          - parentCommentId가 null: 댓글
          - parentCommentId가 있음: 답글

          멘션:
          - 일반 사용자 멘션: mention.userIds에 포함
          - 익명 사용자 멘션: anonymousMentionRequest.anonymousNickname에 닉네임만 포함
          """)
  ResponseEntity<BaseResponse<?>> createComment(
      @Parameter(description = "게시글 ID") Long postId,
      @Parameter(hidden = true) Long userId,
      @Valid CommentSaveRequest request);

  @Operation(summary = "커뮤니티 댓글 조회 API")
  ResponseEntity<BaseResponse<?>> getComments(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "게시글 ID") Long postId,
      @Parameter(description = "차단 회원 댓글 필터링 여부") Boolean isBlockOn);

  @Operation(
      summary = "커뮤니티 댓글 수정 API",
      description =
          """
          댓글 또는 답글의 내용을 수정합니다.

          수정 가능 항목:
          - content: 댓글 내용

          권한:
          - 본인이 작성한 댓글만 수정 가능
          - 삭제된 댓글은 수정 불가
          """)
  ResponseEntity<BaseResponse<?>> updateComment(
      @Parameter(description = "댓글 ID") Long commentId,
      @Parameter(hidden = true) Long userId,
      @Valid CommentUpdateRequest request);

  @Operation(summary = "커뮤니티 댓글 삭제 API")
  ResponseEntity<BaseResponse<?>> deleteComment(
      @Parameter(description = "댓글 ID") Long commentId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "커뮤니티 댓글 신고 API")
  ResponseEntity<BaseResponse<?>> reportComment(
      @Parameter(description = "댓글 ID") Long commentId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "커뮤니티 댓글 좋아요 API")
  ResponseEntity<BaseResponse<?>> likeComment(
      @Parameter(description = "게시글 ID") Long postId,
      @Parameter(description = "댓글 ID") Long commentId,
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "커뮤니티 댓글 좋아요 취소 API")
  ResponseEntity<BaseResponse<?>> unlikeComment(
      @Parameter(description = "게시글 ID") Long postId,
      @Parameter(description = "댓글 ID") Long commentId,
      @Parameter(hidden = true) Long userId);
}
