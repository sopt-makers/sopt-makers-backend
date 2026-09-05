package org.sopt.makers.api.controller.crew.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.post.dto.CreatePostRequest;
import org.sopt.makers.api.controller.crew.post.dto.GetPostsRequest;
import org.sopt.makers.api.controller.crew.post.dto.MentionPostRequest;
import org.sopt.makers.api.controller.crew.post.dto.UpdatePostRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 게시글", description = "CREW 모임 게시글 API")
public interface PostApi {

  @Operation(summary = "모임 게시글 작성")
  ResponseEntity<BaseResponse<?>> createPost(
      CreatePostRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 목록 조회")
  ResponseEntity<BaseResponse<?>> getPosts(
      GetPostsRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 상세 조회")
  ResponseEntity<BaseResponse<?>> getPost(Long postId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 개수 조회")
  ResponseEntity<BaseResponse<?>> getPostCount(Long meetingId);

  @Operation(summary = "모임 게시글 수정")
  ResponseEntity<BaseResponse<?>> updatePost(
      Long postId, UpdatePostRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 신고")
  ResponseEntity<BaseResponse<?>> reportPost(Long postId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 좋아요 토글")
  ResponseEntity<BaseResponse<?>> togglePostLike(
      Long postId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 게시글 조회수 증가")
  ResponseEntity<BaseResponse<?>> increaseViewCount(
      Long postId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "무무 피드 홈 조회")
  ResponseEntity<BaseResponse<?>> getMumuHome(@Parameter(hidden = true) Long userId);

  @Operation(summary = "오늘의 무무 텍스트 조회")
  ResponseEntity<BaseResponse<?>> getMumuText();

  @Operation(summary = "모임 게시글에서 사용자 멘션")
  ResponseEntity<BaseResponse<?>> mentionUsers(
      MentionPostRequest request, @Parameter(hidden = true) Long userId);
}
