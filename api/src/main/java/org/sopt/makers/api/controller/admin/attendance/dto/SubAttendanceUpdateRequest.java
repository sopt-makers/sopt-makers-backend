package org.sopt.makers.api.controller.admin.attendance.dto;

import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;

public record SubAttendanceUpdateRequest(
    @NotNull Long subAttendanceId, @NotNull AttendanceStatus status) {}
