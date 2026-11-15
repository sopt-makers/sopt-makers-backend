package org.sopt.makers.api.controller.admin.attendance.dto;

import java.util.List;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureAttendanceDetail;

public record AttendancesByLectureResponse(List<AttendanceItem> attendances, int totalCount) {
  public static AttendancesByLectureResponse from(
      int totalCount, List<LectureAttendanceDetail> attendances) {
    return new AttendancesByLectureResponse(
        attendances.stream().map(AttendanceItem::from).toList(), totalCount);
  }

  public record AttendanceItem(
      Long attendanceId,
      MemberItem member,
      List<SubAttendanceItem> attendances,
      float updatedScore) {
    static AttendanceItem from(LectureAttendanceDetail attendance) {
      return new AttendanceItem(
          attendance.attendanceId(),
          new MemberItem(attendance.memberId(), attendance.memberName()),
          attendance.attendances().stream().map(SubAttendanceItem::from).toList(),
          attendance.updatedScore());
    }
  }

  public record MemberItem(Long memberId, String name) {}

  public record SubAttendanceItem(
      Long subAttendanceId, int round, AttendanceStatus status, String updateAt) {
    static SubAttendanceItem from(LectureAttendanceDetail.SubAttendanceDetail detail) {
      return new SubAttendanceItem(
          detail.subAttendanceId(), detail.round(), detail.status(), detail.updatedAt().toString());
    }
  }
}
