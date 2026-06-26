package org.sopt.makers.api.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.auth.dto.AuthRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "휴대폰 인증 생성")
  ResponseEntity<BaseResponse<?>> createPhoneVerification(
      AuthRequest.CreatePhoneVerification request);

  @Operation(summary = "휴대폰 인증 확인")
  ResponseEntity<BaseResponse<?>> verifyPhoneVerification(
      AuthRequest.VerifyPhoneVerification request);

  @Operation(summary = "웹 로그인")
  ResponseEntity<BaseResponse<?>> loginFromWeb(AuthRequest.Login request);

  @Operation(summary = "앱 로그인")
  ResponseEntity<BaseResponse<?>> loginFromApp(AuthRequest.Login request);

  @Operation(summary = "회원가입")
  ResponseEntity<BaseResponse<?>> signUp(AuthRequest.SignUp request);

  @Operation(summary = "웹 토큰 갱신")
  ResponseEntity<BaseResponse<?>> refreshFromWeb(String accessToken, String refreshToken);

  @Operation(summary = "앱 토큰 갱신")
  ResponseEntity<BaseResponse<?>> refreshFromApp(AuthRequest.TokenRefreshForApp request);
}
