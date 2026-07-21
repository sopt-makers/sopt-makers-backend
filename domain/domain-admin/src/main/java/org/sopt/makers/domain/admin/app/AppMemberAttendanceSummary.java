package org.sopt.makers.domain.admin.app;

import java.util.List;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.user.UserActivity;

public record AppMemberAttendanceSummary(UserActivity activity, List<Attendance> attendances) {

  public List<Attendance> visibleAttendances() {
    return attendances.stream()
        .filter(
            attendance ->
                !(attendance.attribute() == LectureAttribute.ETC
                    && attendance.status() == AttendanceStatus.NOT_PARTICIPATE))
        .toList();
  }

  public int countByStatus(AttendanceStatus status) {
    return (int) attendances.stream().filter(attendance -> attendance.status() == status).count();
  }
}
