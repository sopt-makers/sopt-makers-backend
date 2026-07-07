package org.sopt.makers.storage.db.admin.projection;

import org.sopt.makers.domain.admin.attendance.AttendanceStatus;

public record AttendanceUserCountRow(Long userId, AttendanceStatus status, Long count) {}
