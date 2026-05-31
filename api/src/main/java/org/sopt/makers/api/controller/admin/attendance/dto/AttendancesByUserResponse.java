package org.sopt.makers.api.controller.admin.attendance.dto;

import java.util.List;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

public record AttendancesByUserResponse(List<AttendanceItem> attendances) {

  public static AttendancesByUserResponse from(List<Attendance> attendances) {
    return new AttendancesByUserResponse(attendances.stream().map(AttendanceItem::from).toList());
  }

  public record AttendanceItem(
      Long id, String attribute, String status, List<SubAttendanceItem> subAttendances) {

    public static AttendanceItem from(Attendance attendance) {
      return new AttendanceItem(
          attendance.getId(),
          attendance.getAttribute().name(),
          attendance.getStatus().name(),
          attendance.getSubAttendances().stream().map(SubAttendanceItem::from).toList());
    }
  }

  public record SubAttendanceItem(Long id, int round, String status) {

    public static SubAttendanceItem from(SubAttendance subAttendance) {
      return new SubAttendanceItem(
          subAttendance.getId(), subAttendance.getRound(), subAttendance.getStatus().name());
    }
  }
}
