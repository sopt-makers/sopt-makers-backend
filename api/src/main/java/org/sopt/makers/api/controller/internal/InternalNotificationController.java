package org.sopt.makers.api.controller.internal;

import static org.sopt.makers.api.controller.internal.InternalNotificationSuccessCode.REGISTER_NOTIFICATION;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.internal.dto.InternalNotificationRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.notification.service.AppNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
@Validated
public class InternalNotificationController implements InternalNotificationApi {

  private final AppNotificationService appNotificationService;

  @Override
  @PostMapping("/notifications")
  public ResponseEntity<BaseResponse<?>> registerNotification(
      @Valid @RequestBody InternalNotificationRequest.Register request) {
    appNotificationService.register(request.toCommand());
    return ResponseFactory.success(REGISTER_NOTIFICATION);
  }
}
