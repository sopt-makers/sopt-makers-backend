package org.sopt.makers.domain.admin.attendance;

import java.time.LocalDateTime;
import java.util.List;

public record LectureAttendanceDetail(
    Long attendanceId,
    Long memberId,
    String memberName,
    float updatedScore,
    List<SubAttendanceDetail> attendances) {
  public record SubAttendanceDetail(
      Long subAttendanceId, int round, AttendanceStatus status, LocalDateTime updatedAt) {}
}
