package org.sopt.makers.api.controller.admin.soptamp.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class AdminSoptampNotificationRequest {

  public record Showcase(
      String nickname,
      Long missionId,
      @NotBlank(message = "알림 제목은 필수입니다") String notificationTitle,
      @NotBlank(message = "알림 본문은 필수입니다") String notificationContent) {}
}
