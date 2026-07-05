package org.sopt.makers.api.controller.auth;

import static org.sopt.makers.api.controller.auth.SocialAccountSuccessCode.GET_SOCIAL_ACCOUNT_PLATFORM;
import static org.sopt.makers.api.controller.auth.SocialAccountSuccessCode.UPDATE_SOCIAL_ACCOUNT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.auth.dto.GetSocialAccountPlatformRequest;
import org.sopt.makers.api.controller.auth.dto.SocialAccountPlatformResponse;
import org.sopt.makers.api.controller.auth.dto.UpdateSocialAccountRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/social/accounts")
@RequiredArgsConstructor
public class SocialAccountController implements SocialAccountApi {

  private final AuthFacade authFacade;

  @Override
  @PostMapping("/platform")
  public ResponseEntity<BaseResponse<?>> getSocialPlatform(
      @Valid @RequestBody GetSocialAccountPlatformRequest request) {
    OAuthPlatform platform = authFacade.getSocialPlatform(request.phone());
    return ResponseFactory.success(
        GET_SOCIAL_ACCOUNT_PLATFORM, new SocialAccountPlatformResponse(platform.name()));
  }

  @Override
  @PatchMapping
  public ResponseEntity<BaseResponse<?>> updateSocialAccount(
      @Valid @RequestBody UpdateSocialAccountRequest request) {
    OAuthPlatform platform = OAuthPlatform.find(request.authPlatform());
    authFacade.updateSocialAccount(request.phone(), request.token(), platform);
    return ResponseFactory.success(UPDATE_SOCIAL_ACCOUNT);
  }
}
