package org.sopt.makers.api.controller.app.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendRequest(@NotNull Long subLectureId, @NotBlank String code) {}
