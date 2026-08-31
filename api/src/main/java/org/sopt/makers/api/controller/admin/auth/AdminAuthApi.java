package org.sopt.makers.api.controller.admin.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.auth.dto.AdminAuthRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin Auth", description = "어드민 인증 API")
public interface AdminAuthApi {

  @Operation(summary = "어드민 로그인")
  ResponseEntity<BaseResponse<?>> login(AdminAuthRequest.AdminLogin request);

  @Operation(summary = "어드민 토큰 갱신")
  ResponseEntity<BaseResponse<?>> refresh(String accessToken, String refreshToken);

  @Operation(summary = "어드민 비밀번호 변경")
  ResponseEntity<BaseResponse<?>> changePassword(
      @Schema(hidden = true) Long adminId, AdminAuthRequest.ChangePassword request);
}
