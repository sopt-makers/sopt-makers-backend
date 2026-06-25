package org.sopt.makers.api.controller.admin.auth;

import static org.sopt.makers.api.controller.admin.auth.AdminAuthSuccessCode.SUCCESS_CHANGE_PASSWORD;
import static org.sopt.makers.api.controller.admin.auth.AdminAuthSuccessCode.SUCCESS_LOGIN;
import static org.sopt.makers.api.controller.admin.auth.AdminAuthSuccessCode.SUCCESS_REFRESH_TOKEN;
import static org.sopt.makers.api.controller.admin.auth.AdminAuthSuccessCode.SUCCESS_SIGN_UP;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.CookieFactory;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.admin.auth.dto.AdminAuthRequest;
import org.sopt.makers.api.controller.admin.auth.dto.AdminAuthResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort.AdminTokenPair;
import org.sopt.makers.domain.admin.auth.service.AdminAuthService;
import org.sopt.makers.domain.admin.auth.service.AdminAuthService.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

  private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

  private final AdminAuthService adminAuthService;
  private final CookieFactory cookieFactory;

  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<?>> signUp(@RequestBody AdminAuthRequest.SignUp request) {
    var adminAccount =
        adminAuthService.signUp(
            request.email(), request.password(), request.name(), request.accountType());
    return ResponseFactory.success(
        SUCCESS_SIGN_UP, AdminAuthResponse.SignUpResult.from(adminAccount));
  }

  @PostMapping("/login")
  public ResponseEntity<BaseResponse<?>> login(@RequestBody AdminAuthRequest.Login request) {
    LoginResult loginResult = adminAuthService.login(request.email(), request.password());
    HttpHeaders headers = cookieFactory.setRefreshToken(loginResult.tokenPair().refreshToken());
    return ResponseFactory.success(
        SUCCESS_LOGIN, headers, AdminAuthResponse.LoginBody.of(loginResult));
  }

  @PostMapping("/refresh")
  public ResponseEntity<BaseResponse<?>> refresh(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
      @CookieValue(value = REFRESH_TOKEN_COOKIE) String refreshToken) {
    AdminTokenPair tokenPair = adminAuthService.refresh(accessToken, refreshToken);
    HttpHeaders headers = cookieFactory.setRefreshToken(tokenPair.refreshToken());
    return ResponseFactory.success(
        SUCCESS_REFRESH_TOKEN, headers, AdminAuthResponse.RefreshResult.from(tokenPair));
  }

  @PatchMapping("/password")
  public ResponseEntity<BaseResponse<?>> changePassword(
      @CurrentUserId Long adminId, @RequestBody AdminAuthRequest.ChangePassword request) {
    adminAuthService.changePassword(adminId, request.oldPassword(), request.newPassword());
    return ResponseFactory.success(SUCCESS_CHANGE_PASSWORD);
  }
}
