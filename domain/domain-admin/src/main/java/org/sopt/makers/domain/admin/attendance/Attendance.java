package org.sopt.makers.domain.admin.attendance;

import java.util.List;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;

public record Attendance(
    Long id,
    Long userId,
    Long lectureId,
    LectureAttribute attribute,
    boolean isEnded,
    AttendanceStatus status,
    List<SubAttendance> subAttendances) {

  public SubAttendance getSubAttendanceByRound(int round) {
    return subAttendances.stream()
        .filter(sub -> sub.round() == round)
        .findFirst()
        .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_SUB_ATTENDANCE));
  }

  public AttendanceStatus computeStatus(SubAttendance updatedSub) {
    List<SubAttendance> updated =
        subAttendances.stream()
            .map(sub -> sub.round() == updatedSub.round() ? updatedSub : sub)
            .toList();

    SubAttendance first = findByRound(updated, 1);
    SubAttendance second = findByRound(updated, 2);

    if (attribute == LectureAttribute.ETC) {
      return second != null && second.status() == AttendanceStatus.ATTENDANCE
          ? AttendanceStatus.PARTICIPATE
          : AttendanceStatus.NOT_PARTICIPATE;
    }

    boolean firstAttendance = first != null && first.status() == AttendanceStatus.ATTENDANCE;
    boolean secondAttendance = second != null && second.status() == AttendanceStatus.ATTENDANCE;
    boolean firstAbsent = first != null && first.status() == AttendanceStatus.ABSENT;
    boolean secondAbsent = second != null && second.status() == AttendanceStatus.ABSENT;

    if (firstAttendance && secondAttendance) {
      return AttendanceStatus.ATTENDANCE;
    }
    if (firstAbsent && secondAbsent) {
      return AttendanceStatus.ABSENT;
    }
    return AttendanceStatus.TARDY;
  }

  public float computeScore() {
    if (attribute == LectureAttribute.ETC) {
      return 0f;
    }
    if (attribute == LectureAttribute.EVENT) {
      return status == AttendanceStatus.ABSENT ? 0f : 0.5f;
    }
    return switch (status) {
      case ABSENT -> -1f;
      case TARDY -> -0.5f;
      default -> 0f;
    };
  }

  private SubAttendance findByRound(List<SubAttendance> list, int round) {
    return list.stream().filter(sub -> sub.round() == round).findFirst().orElse(null);
  }
}
