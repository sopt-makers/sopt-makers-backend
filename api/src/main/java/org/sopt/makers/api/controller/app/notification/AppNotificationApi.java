package org.sopt.makers.api.controller.app.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.springframework.http.ResponseEntity;

@Tag(name = "알림", description = "앱 인앱 알림함 API")
public interface AppNotificationApi {

  @Operation(summary = "알림 목록 조회", description = "카테고리를 주지 않으면 전체 알림을 최신순으로 조회한다.")
  ResponseEntity<BaseResponse<?>> getNotifications(
      @Parameter(hidden = true) Long userId, NotificationCategory category, int page, int size);

  @Operation(summary = "알림 상세 조회")
  ResponseEntity<BaseResponse<?>> getNotificationDetail(
      @Parameter(hidden = true) Long userId, String notificationId);

  @Operation(summary = "알림 읽음 처리", description = "notificationId를 주지 않으면 유저의 알림을 모두 읽음 처리한다.")
  ResponseEntity<BaseResponse<?>> updateNotificationRead(
      @Parameter(hidden = true) Long userId, String notificationId);
}
