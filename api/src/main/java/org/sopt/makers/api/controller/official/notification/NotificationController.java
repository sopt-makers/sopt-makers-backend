package org.sopt.makers.api.controller.official.notification;

import static org.sopt.makers.api.controller.official.notification.NotificationSuccessCode.GET_NOTIFICATION_LIST;
import static org.sopt.makers.api.controller.official.notification.NotificationSuccessCode.REGISTER_NOTIFICATION;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.notification.dto.NotificationRequest;
import org.sopt.makers.api.controller.official.notification.dto.NotificationResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.notification.Notification;
import org.sopt.makers.domain.official.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Validated
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping("/register")
  public ResponseEntity<BaseResponse<?>> register(
      @Valid @RequestBody NotificationRequest.Register request) {
    Notification result = notificationService.register(request.toCommand());
    return ResponseFactory.success(REGISTER_NOTIFICATION, NotificationResponse.Register.of(result));
  }

  @GetMapping("/list")
  public ResponseEntity<BaseResponse<?>> getList(@RequestParam Integer generation) {
    List<Notification> result = notificationService.findByGeneration(generation);
    return ResponseFactory.success(
        GET_NOTIFICATION_LIST, NotificationResponse.NotificationList.of(result));
  }
}
