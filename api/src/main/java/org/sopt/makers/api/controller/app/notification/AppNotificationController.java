package org.sopt.makers.api.controller.app.notification;

import static org.sopt.makers.api.controller.app.notification.AppNotificationSuccessCode.GET_NOTIFICATIONS;
import static org.sopt.makers.api.controller.app.notification.AppNotificationSuccessCode.GET_NOTIFICATION_DETAIL;
import static org.sopt.makers.api.controller.app.notification.AppNotificationSuccessCode.UPDATE_NOTIFICATION_READ;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.notification.dto.NotificationDetailResponse;
import org.sopt.makers.api.controller.app.notification.dto.NotificationSimpleResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.notification.Notification;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.notification.service.AppNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/notification")
@RequiredArgsConstructor
@Validated
public class AppNotificationController implements AppNotificationApi {

  private final AppNotificationService appNotificationService;

  @Override
  @GetMapping("/all")
  public ResponseEntity<BaseResponse<?>> getNotifications(
      @CurrentUserId Long userId,
      @RequestParam(required = false) NotificationCategory category,
      @Min(0) @RequestParam(defaultValue = "0") int page,
      @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size) {
    List<Notification> notifications =
        appNotificationService.getNotifications(userId, category, page, size);
    return ResponseFactory.success(
        GET_NOTIFICATIONS, notifications.stream().map(NotificationSimpleResponse::of).toList());
  }

  @Override
  @GetMapping("/detail/{notificationId}")
  public ResponseEntity<BaseResponse<?>> getNotificationDetail(
      @CurrentUserId Long userId, @PathVariable String notificationId) {
    return ResponseFactory.success(
        GET_NOTIFICATION_DETAIL,
        NotificationDetailResponse.of(
            appNotificationService.getNotification(userId, notificationId)));
  }

  @Override
  @PatchMapping({"/{notificationId}", ""})
  public ResponseEntity<BaseResponse<?>> updateNotificationRead(
      @CurrentUserId Long userId, @PathVariable(required = false) String notificationId) {
    appNotificationService.markAsRead(userId, notificationId);
    return ResponseFactory.success(UPDATE_NOTIFICATION_READ);
  }
}
