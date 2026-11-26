package org.sopt.makers.api.controller.admin.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AdminSoptLetterNotificationRequest(
    @Schema(description = "알림 제목", example = "아직 솝레터를 쓰지 않았어요") @NotBlank(message = "알림 제목은 필수입니다.")
        String notificationTitle,
    @Schema(description = "알림 내용", example = "이번 주제에 한 마디 남겨 보세요")
        @NotBlank(message = "알림 내용은 필수입니다.")
        String notificationContent,
    @Schema(description = "알림을 눌렀을 때 이동할 딥링크", example = "soptletter", nullable = true)
        String deepLink) {}
