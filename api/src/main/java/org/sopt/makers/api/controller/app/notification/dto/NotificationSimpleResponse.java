package org.sopt.makers.api.controller.app.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.sopt.makers.domain.app.notification.Notification;

public record NotificationSimpleResponse(
    @Schema(description = "알림 아이디", example = "01HQ2N8K3P") String notificationId,
    @Schema(description = "알림을 받은 유저 아이디", example = "1") Long userId,
    @Schema(description = "알림 제목", example = "출석 체크가 시작되었어요") String title,
    @Schema(description = "알림 본문", example = "1차 세미나 출석을 진행해주세요") String content,
    @Schema(description = "알림 분류", example = "NEWS") String category,
    @Schema(description = "읽음 여부", example = "false") boolean isRead,
    @Schema(description = "알림이 만들어진 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt) {

  public static NotificationSimpleResponse of(Notification notification) {
    return new NotificationSimpleResponse(
        notification.notificationId(),
        notification.userId(),
        notification.title(),
        notification.content(),
        notification.category().name(),
        notification.isRead(),
        notification.createdAt());
  }
}
