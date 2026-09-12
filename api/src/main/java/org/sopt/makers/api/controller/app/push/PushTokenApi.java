package org.sopt.makers.api.controller.app.push;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.app.push.dto.DeletePushTokenRequest;
import org.sopt.makers.api.controller.app.push.dto.RegisterPushTokenRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "푸시 토큰", description = "앱 기기 푸시 토큰 API")
public interface PushTokenApi {

  @Operation(summary = "푸시 토큰 등록", description = "이미 등록된 토큰이면 아무 일도 일어나지 않는다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "푸시 토큰 등록에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "지원하지 않는 푸시 토큰 플랫폼입니다.", content = @Content),
    @ApiResponse(
        responseCode = "500",
        description = "알림 서버 호출에 실패했습니다. 이때 푸시 토큰은 저장되지 않습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<Void>> registerPushToken(
      @Parameter(hidden = true) Long userId, @Valid RegisterPushTokenRequest request);

  @Operation(summary = "푸시 토큰 해제", description = "알림 서버 해지 호출이 실패해도 성공으로 응답한다.")
  @ApiResponse(responseCode = "200", description = "푸시 토큰 해제에 성공했습니다.")
  ResponseEntity<BaseResponse<Void>> deletePushToken(
      @Parameter(hidden = true) Long userId, @Valid DeletePushTokenRequest request);
}
