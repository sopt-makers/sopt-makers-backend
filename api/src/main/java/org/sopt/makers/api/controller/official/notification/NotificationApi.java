package org.sopt.makers.api.controller.official.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.official.notification.dto.NotificationRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "알림 신청", description = "공식 홈페이지 알림 신청 API")
public interface NotificationApi {

  @Operation(summary = "알림 신청")
  ResponseEntity<BaseResponse<?>> register(@Valid NotificationRequest.Register request);

  @Operation(summary = "알림 신청 목록 조회")
  ResponseEntity<BaseResponse<?>> getList(Integer generation);
}
