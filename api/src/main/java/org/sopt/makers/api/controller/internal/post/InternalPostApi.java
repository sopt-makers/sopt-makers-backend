package org.sopt.makers.api.controller.internal.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.internal.post.dto.InternalPostCreateRequest;
import org.sopt.makers.api.controller.internal.post.dto.InternalPostPageRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "[Internal] 모임 피드", description = "Playground 모임 탭 연동 API")
public interface InternalPostApi {

  @Operation(summary = "[Internal] 사용자의 모임 피드 전체 조회")
  ResponseEntity<BaseResponse<?>> getPosts(Long orgId, InternalPostPageRequest request);

  @Operation(summary = "[Internal] 모임 피드 생성")
  ResponseEntity<BaseResponse<?>> createPost(Long orgId, InternalPostCreateRequest request);
}
