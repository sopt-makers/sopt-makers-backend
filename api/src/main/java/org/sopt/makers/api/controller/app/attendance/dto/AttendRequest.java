package org.sopt.makers.api.controller.app.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendRequest(
    @Schema(description = "출석할 세부 세션 아이디", example = "1") @NotNull Long subLectureId,
    @Schema(description = "운영진이 안내한 출석 코드", example = "1234") @NotBlank String code) {}
