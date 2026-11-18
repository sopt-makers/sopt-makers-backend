package org.sopt.makers.api.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.auth.dto.UpdateSocialAccountRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "소셜 계정", description = "소셜 계정 API")
public interface SocialAccountApi {

  @Operation(summary = "소셜 플랫폼 조회")
  ResponseEntity<BaseResponse<?>> getSocialPlatform(
      @Parameter(description = "이름", required = false) String name,
      @Parameter(description = "전화번호", required = true) String phone);

  @Operation(summary = "소셜 계정 변경")
  ResponseEntity<BaseResponse<?>> updateSocialAccount(UpdateSocialAccountRequest request);
}
