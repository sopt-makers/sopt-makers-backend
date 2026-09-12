package org.sopt.makers.api.controller.app.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.sopt.makers.domain.app.notification.Notification;

public record NotificationDetailResponse(
    @Schema(description = "알림 아이디", example = "01HQ2N8K3P") String notificationId,
    @Schema(description = "알림을 받은 유저 아이디", example = "1") Long userId,
    @Schema(description = "알림 제목", example = "출석 체크가 시작되었어요") String title,
    @Schema(description = "알림 본문", example = "1차 세미나 출석을 진행해주세요") String content,
    @Schema(description = "앱 안에서 열 화면의 딥링크. 없으면 null", example = "home/attendance") String deepLink,
    @Schema(description = "웹으로 열 주소. 없으면 null", example = "https://sopt.org") String webLink,
    @Schema(description = "알림이 만들어진 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt,
    @Schema(description = "알림이 마지막으로 바뀐 일시", example = "2026-09-19T14:00:00")
        LocalDateTime updatedAt) {

  public static NotificationDetailResponse of(Notification notification) {
    return new NotificationDetailResponse(
        notification.notificationId(),
        notification.userId(),
        notification.title(),
        notification.content(),
        notification.deepLink(),
        notification.webLink(),
        notification.createdAt(),
        notification.updatedAt());
  }
}
