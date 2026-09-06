package org.sopt.makers.api.controller.playground.community;

import static org.sopt.makers.api.controller.playground.community.CommunityCommentSuccessCode.DELETE_COMMENT;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.CREATE_POST;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.DELETE_POST;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.GET_POPULAR_POSTS;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.GET_POST;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.GET_POSTS;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.GET_RECENT_POSTS;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.GET_RECENT_SOPTICLE_POSTS;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.GET_TODAY_HOT_POST;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.HIT_POST;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.LIKE_POST;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.REPORT_POST;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.SELECT_VOTE;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.UNLIKE_POST;
import static org.sopt.makers.api.controller.playground.community.CommunitySuccessCode.UPDATE_POST;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.community.dto.CommunityHitRequest;
import org.sopt.makers.api.controller.playground.community.dto.HotPostResponse;
import org.sopt.makers.api.controller.playground.community.dto.PopularPostResponse;
import org.sopt.makers.api.controller.playground.community.dto.PostAllResponse;
import org.sopt.makers.api.controller.playground.community.dto.PostDetailResponse;
import org.sopt.makers.api.controller.playground.community.dto.PostSaveRequest;
import org.sopt.makers.api.controller.playground.community.dto.PostSaveResponse;
import org.sopt.makers.api.controller.playground.community.dto.PostUpdateRequest;
import org.sopt.makers.api.controller.playground.community.dto.PostUpdateResponse;
import org.sopt.makers.api.controller.playground.community.dto.RecentPostResponse;
import org.sopt.makers.api.controller.playground.community.dto.SopticlePostResponse;
import org.sopt.makers.api.controller.playground.community.dto.VoteResponse;
import org.sopt.makers.api.controller.playground.community.dto.VoteSelectionRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityPostListCategory;
import org.sopt.makers.domain.playground.community.CommunityPostListFilter;
import org.sopt.makers.domain.playground.community.comment.service.CommentCommandService;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostCommandService;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService;
import org.sopt.makers.domain.playground.community.vote.service.VoteCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("playgroundCommunityController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityController implements CommunityApi {

  private final CommunityPostQueryService communityPostQueryService;
  private final CommunityPostCommandService communityPostCommandService;
  private final VoteCommandService voteCommandService;
  private final CommentCommandService commentCommandService;

  @Override
  @GetMapping("/posts/{postId}")
  public ResponseEntity<BaseResponse<?>> getOnePost(
      @CurrentUserId Long userId,
      @PathVariable("postId") Long postId,
      @RequestParam(value = "isBlockOn", required = false, defaultValue = "true") Boolean isBlockOn) {
    return ResponseFactory.success(
        GET_POST, PostDetailResponse.from(communityPostQueryService.getPostDetail(userId, postId, isBlockOn)));
  }

  @Override
  @GetMapping("/posts")
  public ResponseEntity<BaseResponse<?>> getAllPosts(
      @CurrentUserId Long userId,
      @RequestParam(required = false) CommunityCategoryCode categoryCode,
      @RequestParam(required = false) CommunityPostListCategory category,
      @RequestParam(required = false) CommunityPostListFilter filter,
      @RequestParam(value = "isBlockOn", required = false, defaultValue = "true") Boolean isBlockOn,
      @RequestParam(required = false) Integer limit,
      @RequestParam(required = false) String cursor) {
    return ResponseFactory.success(
        GET_POSTS,
        PostAllResponse.from(
            communityPostQueryService.getPosts(userId, categoryCode, category, filter, isBlockOn, limit, cursor)));
  }

  @Override
  @GetMapping("/posts/popular")
  public ResponseEntity<BaseResponse<?>> getPopularPosts(
      @CurrentUserId Long userId, @RequestParam(defaultValue = "3") int limit) {
    List<PopularPostResponse> responses =
        communityPostQueryService.getPopularPosts(userId, limit).stream()
            .map(PopularPostResponse::from)
            .toList();
    return ResponseFactory.success(GET_POPULAR_POSTS, responses);
  }

  @Override
  @GetMapping("/posts/sopticle")
  public ResponseEntity<BaseResponse<?>> getRecentSopticlePosts() {
    List<SopticlePostResponse> responses =
        communityPostQueryService.getRecentSopticlePosts().stream().map(SopticlePostResponse::from).toList();
    return ResponseFactory.success(GET_RECENT_SOPTICLE_POSTS, responses);
  }

  @Override
  @GetMapping("/posts/all/recent")
  public ResponseEntity<BaseResponse<?>> getRecentPosts(@CurrentUserId Long userId) {
    List<RecentPostResponse> responses =
        communityPostQueryService.getRecentPosts(userId).stream().map(RecentPostResponse::from).toList();
    return ResponseFactory.success(GET_RECENT_POSTS, responses);
  }

  @Deprecated
  @Override
  @GetMapping("/posts/hot")
  public ResponseEntity<BaseResponse<?>> getTodayHotPost() {
    Post hotPost = communityPostQueryService.getTodayHotPost().orElse(null);
    return ResponseFactory.success(GET_TODAY_HOT_POST, HotPostResponse.of(hotPost));
  }

  @Override
  @PostMapping("/posts/hit")
  public ResponseEntity<BaseResponse<?>> upPostHit(
      @CurrentUserId Long userId, @RequestBody @Valid CommunityHitRequest request) {
    communityPostCommandService.increaseHit(userId, request.postIdList());
    return ResponseFactory.success(HIT_POST);
  }

  @Override
  @PostMapping("/posts")
  public ResponseEntity<BaseResponse<?>> createPost(
      @CurrentUserId Long userId, @RequestBody @Valid PostSaveRequest request) {
    return ResponseFactory.success(
        CREATE_POST,
        PostSaveResponse.from(communityPostCommandService.createPost(userId, request.toCommand())));
  }

  @Override
  @PutMapping("/posts")
  public ResponseEntity<BaseResponse<?>> updatePost(
      @CurrentUserId Long userId, @RequestBody PostUpdateRequest request) {
    return ResponseFactory.success(
        UPDATE_POST,
        PostUpdateResponse.from(
            communityPostCommandService.updatePost(userId, request.postId(), request.toCommand())));
  }

  @Override
  @DeleteMapping("/posts/{postId}")
  public ResponseEntity<BaseResponse<?>> deletePost(
      @PathVariable("postId") Long postId, @CurrentUserId Long userId) {
    communityPostCommandService.deletePost(userId, postId);
    return ResponseFactory.success(DELETE_POST);
  }

  @Override
  @PostMapping("/posts/like/{postId}")
  public ResponseEntity<BaseResponse<?>> likePost(
      @PathVariable("postId") Long postId, @CurrentUserId Long userId) {
    communityPostCommandService.likePost(userId, postId);
    return ResponseFactory.success(LIKE_POST);
  }

  @Override
  @DeleteMapping("/posts/unlike/{postId}")
  public ResponseEntity<BaseResponse<?>> unlikePost(
      @PathVariable("postId") Long postId, @CurrentUserId Long userId) {
    communityPostCommandService.unlikePost(userId, postId);
    return ResponseFactory.success(UNLIKE_POST);
  }

  @Override
  @PostMapping("/posts/{postId}/report")
  public ResponseEntity<BaseResponse<?>> reportPost(
      @PathVariable("postId") Long postId, @CurrentUserId Long userId) {
    communityPostCommandService.reportPost(userId, postId);
    return ResponseFactory.success(REPORT_POST);
  }

  @Override
  @PostMapping("/posts/{postId}/vote")
  public ResponseEntity<BaseResponse<?>> selectVote(
      @CurrentUserId Long userId,
      @PathVariable("postId") Long postId,
      @RequestBody VoteSelectionRequest request) {
    return ResponseFactory.success(
        SELECT_VOTE, VoteResponse.from(voteCommandService.selectVote(userId, postId, request.selectedOptions())));
  }

  @Override
  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<BaseResponse<?>> deleteComment(
      @PathVariable("commentId") Long commentId, @CurrentUserId Long userId) {
    commentCommandService.deleteComment(userId, commentId);
    return ResponseFactory.success(DELETE_COMMENT);
  }
}
