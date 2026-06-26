package org.sopt.makers.api.controller.auth;

import static org.sopt.makers.api.controller.auth.AuthSuccessCode.CREATE_PHONE_VERIFICATION;
import static org.sopt.makers.api.controller.auth.AuthSuccessCode.LOGIN;
import static org.sopt.makers.api.controller.auth.AuthSuccessCode.REFRESH_TOKEN;
import static org.sopt.makers.api.controller.auth.AuthSuccessCode.SIGNUP;
import static org.sopt.makers.api.controller.auth.AuthSuccessCode.VERIFY_PHONE_VERIFICATION;
import static org.sopt.makers.domain.auth.exception.AuthFailure.REFRESH_TOKEN_NOT_FOUND;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.CookieFactory;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.auth.dto.AuthRequest;
import org.sopt.makers.api.controller.auth.dto.AuthResponse;
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
@Tag(name = "인증", description = "인증 API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

  private final AuthFacade authFacade;
  private final CookieFactory cookieFactory;

  @PostMapping("/phone")
  public ResponseEntity<BaseResponse<?>> createPhoneVerification(
      @RequestBody AuthRequest.CreatePhoneVerification request) {
    PhoneVerificationType type = PhoneVerificationType.find(request.verificationType());
    authFacade.createPhoneVerification(request.userId(), request.phone(), type);
    return ResponseFactory.success(CREATE_PHONE_VERIFICATION);
  }

  @PostMapping("/verify/phone")
  public ResponseEntity<BaseResponse<?>> verifyPhoneVerification(
      @RequestBody AuthRequest.VerifyPhoneVerification request) {
    PhoneVerificationType type = PhoneVerificationType.find(request.verificationType());
    AuthFacade.VerifyResult result =
        authFacade.verifyPhoneCode(request.phone(), request.code(), type);
    return ResponseFactory.success(
        VERIFY_PHONE_VERIFICATION, AuthResponse.VerifyPhoneResult.from(result));
  }

  @PostMapping("/login/web")
  public ResponseEntity<BaseResponse<?>> loginFromWeb(@RequestBody AuthRequest.Login request) {
    OAuthPlatform platform = OAuthPlatform.find(request.authPlatform());
    AuthFacade.LoginResult result = authFacade.login(request.idToken(), platform);
    HttpHeaders headers = cookieFactory.setRefreshToken(result.refreshToken());
    return ResponseFactory.success(LOGIN, headers, AuthResponse.LoginForWebResult.from(result));
  }

  @PostMapping("/login/app")
  public ResponseEntity<BaseResponse<?>> loginFromApp(@RequestBody AuthRequest.Login request) {
    OAuthPlatform platform = OAuthPlatform.find(request.authPlatform());
    AuthFacade.LoginResult result = authFacade.login(request.idToken(), platform);
    return ResponseFactory.success(LOGIN, AuthResponse.LoginForAppResult.from(result));
  }

  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<?>> signUp(@RequestBody AuthRequest.SignUp request) {
    OAuthPlatform platform = OAuthPlatform.find(request.authPlatform());
    authFacade.signUp(request.idToken(), request.phone(), platform);
    return ResponseFactory.success(SIGNUP);
  }

  @PostMapping("/refresh/web")
  public ResponseEntity<BaseResponse<?>> refreshFromWeb(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
      @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
    if (refreshToken == null) {
      throw new AuthException(REFRESH_TOKEN_NOT_FOUND);
    }
    TokenIssuerPort.TokenPair tokenPair = authFacade.refresh(accessToken, refreshToken);
    HttpHeaders headers = cookieFactory.setRefreshToken(tokenPair.refreshToken());
    return ResponseFactory.success(
        REFRESH_TOKEN, headers, AuthResponse.RefreshForWebResult.from(tokenPair));
  }

  @PostMapping("/refresh/app")
  public ResponseEntity<BaseResponse<?>> refreshFromApp(
      @RequestBody AuthRequest.TokenRefreshForApp request) {
    TokenIssuerPort.TokenPair tokenPair =
        authFacade.refresh(request.accessToken(), request.refreshToken());
    return ResponseFactory.success(REFRESH_TOKEN, AuthResponse.RefreshForAppResult.from(tokenPair));
  }
}
