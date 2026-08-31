package org.sopt.makers.api.controller.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.internal.dto.InternalNotificationRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "[Internal] 알림", description = "알림 서버가 호출하는 인앱 알림 적재 API")
public interface InternalNotificationApi {

  @Operation(summary = "인앱 알림 등록", description = "알림 서버가 발송한 알림을 수신자별로 알림함에 적재한다.")
  ResponseEntity<BaseResponse<?>> registerNotification(
      @Valid InternalNotificationRequest.Register request);
}
