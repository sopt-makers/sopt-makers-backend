package org.sopt.makers.api.controller.admin.attendance.dto;

import java.util.List;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

public record AttendancesByLectureResponse(int totalCount, List<AttendanceItem> attendances) {

  public static AttendancesByLectureResponse from(int totalCount, List<Attendance> attendances) {
    return new AttendancesByLectureResponse(
        totalCount, attendances.stream().map(AttendanceItem::from).toList());
  }

  public record AttendanceItem(
      Long attendanceId, Long userId, String status, List<SubAttendanceItem> subAttendances) {

    public static AttendanceItem from(Attendance attendance) {
      return new AttendanceItem(
          attendance.id(),
          attendance.userId(),
          attendance.status().name(),
          attendance.subAttendances().stream().map(SubAttendanceItem::from).toList());
    }
  }

  public record SubAttendanceItem(Long subAttendanceId, int round, String status) {

    public static SubAttendanceItem from(SubAttendance subAttendance) {
      return new SubAttendanceItem(
          subAttendance.id(), subAttendance.round(), subAttendance.status().name());
    }
  }
}
