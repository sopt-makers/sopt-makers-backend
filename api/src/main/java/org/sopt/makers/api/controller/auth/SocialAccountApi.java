package org.sopt.makers.api.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.auth.dto.GetSocialAccountPlatformRequest;
import org.sopt.makers.api.controller.auth.dto.SocialAccountRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "소셜 계정", description = "소셜 계정 API")
public interface SocialAccountApi {

  @Operation(summary = "소셜 플랫폼 조회")
  ResponseEntity<BaseResponse<?>> getSocialPlatform(GetSocialAccountPlatformRequest request);

  @Operation(summary = "소셜 계정 변경")
  ResponseEntity<BaseResponse<?>> updateSocialAccount(
      SocialAccountRequest.UpdateSocialAccount request);
}
