package org.sopt.makers.api.controller.internal.post;

import static org.sopt.makers.api.controller.internal.post.InternalPostSuccessCode.CREATE_POST;
import static org.sopt.makers.api.controller.internal.post.InternalPostSuccessCode.GET_POSTS;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.internal.post.dto.InternalPostCreateRequest;
import org.sopt.makers.api.controller.internal.post.dto.InternalPostCreateResponse;
import org.sopt.makers.api.controller.internal.post.dto.InternalPostPageRequest;
import org.sopt.makers.api.controller.internal.post.dto.InternalPostPageResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/post")
@RequiredArgsConstructor
public class InternalPostController implements InternalPostApi {

  private final PostService postService;

  @Override
  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<?>> getPosts(
      @PathVariable Long orgId, @Valid @ModelAttribute InternalPostPageRequest request) {
    return ResponseFactory.success(
        GET_POSTS,
        InternalPostPageResponse.from(
            postService.findMeetingPosts(
                null, orgId, request.pageOrDefault(), request.takeOrDefault())));
  }

  @Override
  @PostMapping("/{orgId}")
  public ResponseEntity<BaseResponse<?>> createPost(
      @PathVariable Long orgId, @Valid @RequestBody InternalPostCreateRequest request) {
    Post post = postService.createMeetingPost(request.toCommand(), orgId);
    return ResponseFactory.success(CREATE_POST, new InternalPostCreateResponse(post.id()));
  }
}
