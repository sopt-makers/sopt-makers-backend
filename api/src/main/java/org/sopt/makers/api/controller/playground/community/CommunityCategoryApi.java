package org.sopt.makers.api.controller.playground.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "커뮤니티 카테고리 관련 API", description = "커뮤니티 카테고리 관련 API List")
@SecurityRequirement(name = "Authorization")
public interface CommunityCategoryApi {

  @Operation(summary = "커뮤니티 카테고리 전체 조회 API")
  ResponseEntity<BaseResponse<?>> getAllCategories();
}
