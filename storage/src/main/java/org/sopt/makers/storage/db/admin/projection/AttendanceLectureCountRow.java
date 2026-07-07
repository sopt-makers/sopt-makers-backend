package org.sopt.makers.storage.db.admin.projection;

import org.sopt.makers.domain.admin.attendance.AttendanceStatus;

public record AttendanceLectureCountRow(Long lectureId, AttendanceStatus status, Long count) {}
