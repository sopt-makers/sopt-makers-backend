package org.sopt.makers.api.controller.app.push;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.app.push.dto.DeletePushTokenRequest;
import org.sopt.makers.api.controller.app.push.dto.RegisterPushTokenRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "푸시 토큰", description = "앱 기기 푸시 토큰 API")
public interface PushTokenApi {

  @Operation(summary = "푸시 토큰 등록", description = "이미 등록된 토큰이면 아무 일도 일어나지 않는다.")
  ResponseEntity<BaseResponse<?>> registerPushToken(
      @Parameter(hidden = true) Long userId, @Valid RegisterPushTokenRequest request);

  @Operation(summary = "푸시 토큰 해제")
  ResponseEntity<BaseResponse<?>> deletePushToken(
      @Parameter(hidden = true) Long userId, @Valid DeletePushTokenRequest request);
}
