package org.sopt.makers.api.controller.app.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AttendResponse(
    @Schema(description = "출석 처리한 세부 세션 아이디", example = "1") long subLectureId) {}
