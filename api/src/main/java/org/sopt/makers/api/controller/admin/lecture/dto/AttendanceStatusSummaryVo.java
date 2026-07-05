package org.sopt.makers.api.controller.admin.lecture.dto;

import org.sopt.makers.domain.admin.attendance.AdminLecture;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.service.AdminLectureService;

public record AttendanceStatusSummaryVo(int attendance, int absent, int tardy, int unknown) {

  public static AttendanceStatusSummaryVo from(AdminLecture lecture, AdminLectureService service) {
    boolean isEnded = lecture.isEnd();
    int absentCount = service.countAttendanceByStatus(lecture.id(), AttendanceStatus.ABSENT);
    return new AttendanceStatusSummaryVo(
        service.countAttendanceByStatus(lecture.id(), AttendanceStatus.ATTENDANCE),
        isEnded ? absentCount : 0,
        service.countAttendanceByStatus(lecture.id(), AttendanceStatus.TARDY),
        isEnded ? 0 : absentCount);
  }
}
