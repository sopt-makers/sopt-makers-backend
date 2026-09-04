package org.sopt.makers.api.controller.playground.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.playground.community.dto.CommunityHitRequest;
import org.sopt.makers.api.controller.playground.community.dto.PostSaveRequest;
import org.sopt.makers.api.controller.playground.community.dto.PostUpdateRequest;
import org.sopt.makers.api.controller.playground.community.dto.VoteSelectionRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityPostListCategory;
import org.sopt.makers.domain.playground.community.CommunityPostListFilter;
import org.springframework.http.ResponseEntity;

@Tag(name = "Community 관련 API", description = "Community 관련 API List")
@SecurityRequirement(name = "Authorization")
public interface CommunityApi {

  @Operation(summary = "커뮤니티 글 상세 조회")
  ResponseEntity<BaseResponse<?>> getOnePost(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "게시글 ID") Long postId,
      @Parameter(description = "차단 회원 게시글 필터링 여부") Boolean isBlockOn);

  @Operation(
      summary = "커뮤니티 글 목록 조회",
      description =
          """
          categoryCode: 신규 단일 카테고리 코드. 값이 있으면 category/filter보다 우선 적용된다.
          category: FREE, PROMOTION, SOPTICLE (categoryCode가 없을 때 사용하는 legacy 파라미터)
          filter:
            - FREE: 사용하지 않음
            - PROMOTION: ALL, EVENT, PROJECT, RECRUIT, ETC
            - SOPTICLE: ALL, PLAN, DESIGN, SERVER, WEB, IOS, ANDROID, ETC
          cursor: 처음 조회 시 null, 이후 응답의 nextCursor 사용
          """)
  ResponseEntity<BaseResponse<?>> getAllPosts(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "신규 단일 카테고리 코드") CommunityCategoryCode categoryCode,
      @Parameter(description = "legacy 카테고리") CommunityPostListCategory category,
      @Parameter(description = "legacy 필터") CommunityPostListFilter filter,
      @Parameter(description = "차단 회원 게시글 필터링 여부") Boolean isBlockOn,
      @Parameter(description = "조회 개수") Integer limit,
      @Parameter(description = "무한스크롤 cursor") String cursor);

  @Operation(summary = "커뮤니티 홈 인기글 조회 API")
  ResponseEntity<BaseResponse<?>> getPopularPosts(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "조회할 인기글 개수 (기본값: 3)") int limit);

  @Operation(summary = "커뮤니티 홈 최근 솝티클 목록 조회 API")
  ResponseEntity<BaseResponse<?>> getRecentSopticlePosts();

  @Operation(summary = "커뮤니티 홈 모든 카테고리 최신글 조회 API")
  ResponseEntity<BaseResponse<?>> getRecentPosts(@Parameter(hidden = true) Long userId);

  @Deprecated
  @Operation(summary = "핫 게시물 조회 API")
  ResponseEntity<BaseResponse<?>> getTodayHotPost();

  @Operation(summary = "커뮤니티 글 조회수 증가")
  ResponseEntity<BaseResponse<?>> upPostHit(@Valid CommunityHitRequest request);

  @Operation(summary = "커뮤니티 글 생성")
  ResponseEntity<BaseResponse<?>> createPost(
      @Parameter(hidden = true) Long userId, @Valid PostSaveRequest request);

  @Operation(summary = "커뮤니티 글 수정")
  ResponseEntity<BaseResponse<?>> updatePost(
      @Parameter(hidden = true) Long userId, PostUpdateRequest request);

  @Operation(summary = "커뮤니티 글 삭제")
  ResponseEntity<BaseResponse<?>> deletePost(
      @Parameter(description = "게시글 ID") Long postId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "커뮤니티 게시글 좋아요 API")
  ResponseEntity<BaseResponse<?>> likePost(
      @Parameter(description = "게시글 ID") Long postId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "커뮤니티 게시글 좋아요 취소 API")
  ResponseEntity<BaseResponse<?>> unlikePost(
      @Parameter(description = "게시글 ID") Long postId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "커뮤니티 글 신고 API")
  ResponseEntity<BaseResponse<?>> reportPost(
      @Parameter(description = "게시글 ID") Long postId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "커뮤니티 게시글 투표 선택 API")
  ResponseEntity<BaseResponse<?>> selectVote(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "게시글 ID") Long postId,
      VoteSelectionRequest request);
}
