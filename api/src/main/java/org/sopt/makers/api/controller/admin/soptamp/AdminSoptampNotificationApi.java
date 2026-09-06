package org.sopt.makers.api.controller.admin.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.soptamp.dto.AdminSoptampNotificationRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 솝탬프 알림", description = "어드민 솝탬프 알림 API. apiKey 헤더로 인증한다")
public interface AdminSoptampNotificationApi {

  @Operation(summary = "솝탬프 유저들에게 특정 솝탬프에 대한 알림 전송")
  ResponseEntity<BaseResponse<?>> sendSoptampShowcase(
      @Parameter(description = "알림 API 키", required = true) String apiKey,
      AdminSoptampNotificationRequest.Showcase request);
}
