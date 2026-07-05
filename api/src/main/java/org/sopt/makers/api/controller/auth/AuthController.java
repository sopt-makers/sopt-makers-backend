package org.sopt.makers.api.controller.auth;

import static org.sopt.makers.api.controller.auth.AuthSuccessCode.CREATE_PHONE_VERIFICATION;
import static org.sopt.makers.api.controller.auth.AuthSuccessCode.LOGIN;
import static org.sopt.makers.api.controller.auth.AuthSuccessCode.REFRESH_TOKEN;
import static org.sopt.makers.api.controller.auth.AuthSuccessCode.SIGNUP;
import static org.sopt.makers.api.controller.auth.AuthSuccessCode.VERIFY_PHONE_VERIFICATION;
import static org.sopt.makers.domain.auth.exception.AuthFailure.REFRESH_TOKEN_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.CookieFactory;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.auth.dto.CreatePhoneVerificationRequest;
import org.sopt.makers.api.controller.auth.dto.LoginForAppResponse;
import org.sopt.makers.api.controller.auth.dto.LoginForWebResponse;
import org.sopt.makers.api.controller.auth.dto.LoginRequest;
import org.sopt.makers.api.controller.auth.dto.RefreshForAppResponse;
import org.sopt.makers.api.controller.auth.dto.RefreshForWebResponse;
import org.sopt.makers.api.controller.auth.dto.SignUpRequest;
import org.sopt.makers.api.controller.auth.dto.TokenRefreshForAppRequest;
import org.sopt.makers.api.controller.auth.dto.VerifyPhoneResponse;
import org.sopt.makers.api.controller.auth.dto.VerifyPhoneVerificationRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

  private final AuthFacade authFacade;
  private final CookieFactory cookieFactory;

  @Override
  @PostMapping("/phone")
  public ResponseEntity<BaseResponse<?>> createPhoneVerification(
      @RequestBody CreatePhoneVerificationRequest request) {
    PhoneVerificationType type = PhoneVerificationType.find(request.type());
    authFacade.createPhoneVerification(request.userId(), request.phone(), type);
    return ResponseFactory.success(CREATE_PHONE_VERIFICATION);
  }

  @Override
  @PostMapping("/verify/phone")
  public ResponseEntity<BaseResponse<?>> verifyPhoneVerification(
      @RequestBody VerifyPhoneVerificationRequest request) {
    PhoneVerificationType type = PhoneVerificationType.find(request.type());
    AuthFacade.VerifyResult result = authFacade.verifyPhoneCode(request.phone(), request.code(), type);
    return ResponseFactory.success(VERIFY_PHONE_VERIFICATION, VerifyPhoneResponse.from(result));
  }

  @Override
  @PostMapping("/login/web")
  public ResponseEntity<BaseResponse<?>> loginFromWeb(@RequestBody LoginRequest request) {
    OAuthPlatform platform = OAuthPlatform.find(request.authPlatform());
    AuthFacade.LoginResult result = authFacade.login(request.token(), platform);
    HttpHeaders headers = cookieFactory.setRefreshToken(result.refreshToken());
    return ResponseFactory.success(LOGIN, headers, LoginForWebResponse.from(result));
  }

  @Override
  @PostMapping("/login/app")
  public ResponseEntity<BaseResponse<?>> loginFromApp(@RequestBody LoginRequest request) {
    OAuthPlatform platform = OAuthPlatform.find(request.authPlatform());
    AuthFacade.LoginResult result = authFacade.login(request.token(), platform);
    return ResponseFactory.success(LOGIN, LoginForAppResponse.from(result));
  }

  @Override
  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<?>> signUp(@RequestBody SignUpRequest request) {
    OAuthPlatform platform = OAuthPlatform.find(request.authPlatform());
    authFacade.signUp(request.token(), request.phone(), platform);
    return ResponseFactory.success(SIGNUP);
  }

  @Override
  @PostMapping("/refresh/web")
  public ResponseEntity<BaseResponse<?>> refreshFromWeb(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
      @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
    if (refreshToken == null) {
      throw new AuthException(REFRESH_TOKEN_NOT_FOUND);
    }
    TokenIssuerPort.TokenPair tokenPair = authFacade.refresh(accessToken, refreshToken);
    HttpHeaders headers = cookieFactory.setRefreshToken(tokenPair.refreshToken());
    return ResponseFactory.success(REFRESH_TOKEN, headers, RefreshForWebResponse.from(tokenPair));
  }

  @Override
  @PostMapping("/refresh/app")
  public ResponseEntity<BaseResponse<?>> refreshFromApp(
      @RequestBody TokenRefreshForAppRequest request) {
    TokenIssuerPort.TokenPair tokenPair =
        authFacade.refresh(request.accessToken(), request.refreshToken());
    return ResponseFactory.success(REFRESH_TOKEN, RefreshForAppResponse.from(tokenPair));
  }
}
