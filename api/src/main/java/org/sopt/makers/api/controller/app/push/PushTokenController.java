package org.sopt.makers.api.controller.app.push;

import static org.sopt.makers.api.controller.app.push.PushTokenSuccessCode.DELETE_PUSH_TOKEN;
import static org.sopt.makers.api.controller.app.push.PushTokenSuccessCode.REGISTER_PUSH_TOKEN;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.push.dto.DeletePushTokenRequest;
import org.sopt.makers.api.controller.app.push.dto.RegisterPushTokenRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.push.service.PushTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/user/push-token")
@RequiredArgsConstructor
@Validated
public class PushTokenController implements PushTokenApi {

  private final PushTokenService pushTokenService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> registerPushToken(
      @CurrentUserId Long userId, @Valid @RequestBody RegisterPushTokenRequest request) {
    pushTokenService.register(userId, request.pushToken(), request.toPlatform());
    return ResponseFactory.success(REGISTER_PUSH_TOKEN);
  }

  @Override
  @DeleteMapping
  public ResponseEntity<BaseResponse<?>> deletePushToken(
      @CurrentUserId Long userId, @Valid @RequestBody DeletePushTokenRequest request) {
    pushTokenService.delete(userId, request.pushToken());
    return ResponseFactory.success(DELETE_PUSH_TOKEN);
  }
}
