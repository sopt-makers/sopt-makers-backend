package org.sopt.makers.api.controller.crew.post;

import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.CREATE_POST;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.GET_MUMU_HOME;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.GET_MUMU_TEXT;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.GET_POST;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.GET_POSTS;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.GET_POST_COUNT;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.INCREASE_POST_VIEW_COUNT;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.MENTION_POST_USERS;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.REPORT_POST;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.TOGGLE_POST_LIKE;
import static org.sopt.makers.api.controller.crew.post.PostSuccessCode.UPDATE_POST;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.post.dto.CreatePostRequest;
import org.sopt.makers.api.controller.crew.post.dto.CreatePostResponse;
import org.sopt.makers.api.controller.crew.post.dto.GetPostsRequest;
import org.sopt.makers.api.controller.crew.post.dto.MentionPostRequest;
import org.sopt.makers.api.controller.crew.post.dto.MumuHomeResponse;
import org.sopt.makers.api.controller.crew.post.dto.MumuTextResponse;
import org.sopt.makers.api.controller.crew.post.dto.PostCountResponse;
import org.sopt.makers.api.controller.crew.post.dto.PostDetailResponse;
import org.sopt.makers.api.controller.crew.post.dto.PostPageResponse;
import org.sopt.makers.api.controller.crew.post.dto.PostViewCountResponse;
import org.sopt.makers.api.controller.crew.post.dto.ReportPostResponse;
import org.sopt.makers.api.controller.crew.post.dto.TogglePostLikeResponse;
import org.sopt.makers.api.controller.crew.post.dto.UpdatePostRequest;
import org.sopt.makers.api.controller.crew.post.dto.UpdatePostResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.report.PostReport;
import org.sopt.makers.domain.playground.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post/v2")
@RequiredArgsConstructor
public class PostController implements PostApi {

  private final PostService postService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createPost(
      @Valid @RequestBody CreatePostRequest request, @CurrentUserId Long userId) {
    Post post = postService.createMeetingPost(request.toCommand(), userId);
    return ResponseFactory.success(CREATE_POST, new CreatePostResponse(post.id()));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getPosts(
      @Valid @ModelAttribute GetPostsRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_POSTS,
        PostPageResponse.from(
            postService.findMeetingPosts(
                request.meetingId(), userId, request.pageOrDefault(), request.takeOrDefault())));
  }

  @Override
  @GetMapping("/{postId}")
  public ResponseEntity<BaseResponse<?>> getPost(
      @PathVariable Long postId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_POST, PostDetailResponse.from(postService.getMeetingPost(postId, userId)));
  }

  @Override
  @GetMapping("/count")
  public ResponseEntity<BaseResponse<?>> getPostCount(@RequestParam Long meetingId) {
    return ResponseFactory.success(
        GET_POST_COUNT, new PostCountResponse(postService.countMeetingPosts(meetingId)));
  }

  @Override
  @PutMapping("/{postId}")
  public ResponseEntity<BaseResponse<?>> updatePost(
      @PathVariable Long postId,
      @Valid @RequestBody UpdatePostRequest request,
      @CurrentUserId Long userId) {
    return ResponseFactory.success(
        UPDATE_POST,
        UpdatePostResponse.from(
            postService.updatePost(postId, request.toCommand(), userId).post()));
  }

  @Override
  @PostMapping("/{postId}/report")
  public ResponseEntity<BaseResponse<?>> reportPost(
      @PathVariable Long postId, @CurrentUserId Long userId) {
    PostReport report = postService.reportPost(postId, userId);
    return ResponseFactory.success(REPORT_POST, new ReportPostResponse(report.id()));
  }

  @Override
  @PostMapping("/{postId}/like")
  public ResponseEntity<BaseResponse<?>> togglePostLike(
      @PathVariable Long postId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        TOGGLE_POST_LIKE, new TogglePostLikeResponse(postService.togglePostLike(postId, userId)));
  }

  @Override
  @PostMapping("/{postId}/views")
  public ResponseEntity<BaseResponse<?>> increaseViewCount(
      @PathVariable Long postId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        INCREASE_POST_VIEW_COUNT,
        new PostViewCountResponse(postService.increaseViewCount(postId, userId)));
  }

  @Override
  @GetMapping("/mumu/home")
  public ResponseEntity<BaseResponse<?>> getMumuHome(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MUMU_HOME, MumuHomeResponse.from(postService.getMumuHome(userId)));
  }

  @Override
  @GetMapping("/mumuText")
  public ResponseEntity<BaseResponse<?>> getMumuText() {
    return ResponseFactory.success(
        GET_MUMU_TEXT, new MumuTextResponse(postService.getCurrentMumuText()));
  }

  @Override
  @PostMapping("/mention")
  public ResponseEntity<BaseResponse<?>> mentionUsers(
      @Valid @RequestBody MentionPostRequest request, @CurrentUserId Long userId) {
    postService.mentionUsers(request.postId(), request.orgIds(), request.content(), userId);
    return ResponseFactory.success(MENTION_POST_USERS);
  }
}
