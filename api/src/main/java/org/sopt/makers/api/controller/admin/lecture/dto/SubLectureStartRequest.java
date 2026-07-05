package org.sopt.makers.api.controller.admin.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubLectureStartRequest(
    @NotNull Long lectureId, @NotNull Integer round, @NotBlank String code) {}
