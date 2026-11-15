package org.sopt.makers.domain.admin.attendance;

import java.time.LocalDateTime;
import java.util.List;

public record MemberAttendanceDetail(
    String lectureName,
    LocalDateTime startDate,
    LocalDateTime endDate,
    float additiveScore,
    AttendanceStatus status,
    List<SubAttendanceDetail> subAttendances) {

  public record SubAttendanceDetail(int round, AttendanceStatus status, LocalDateTime updatedAt) {}
}
