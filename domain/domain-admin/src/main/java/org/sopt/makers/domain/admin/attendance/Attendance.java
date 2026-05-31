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

  private static final int FIRST_ROUND = 1;
  private static final int SECOND_ROUND = 2;
  private static final float NO_DEDUCTION = 0f;
  private static final float EVENT_PARTICIPATION_SCORE = 0.5f;
  private static final float TARDY_DEDUCTION = -0.5f;
  private static final float ABSENT_DEDUCTION = -1f;

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

    SubAttendance first = findByRound(updated, FIRST_ROUND);
    SubAttendance second = findByRound(updated, SECOND_ROUND);

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
      return NO_DEDUCTION;
    }
    if (attribute == LectureAttribute.EVENT) {
      return status == AttendanceStatus.ABSENT ? NO_DEDUCTION : EVENT_PARTICIPATION_SCORE;
    }
    return switch (status) {
      case ABSENT -> ABSENT_DEDUCTION;
      case TARDY -> TARDY_DEDUCTION;
      default -> NO_DEDUCTION;
    };
  }

  private SubAttendance findByRound(List<SubAttendance> list, int round) {
    return list.stream().filter(sub -> sub.round() == round).findFirst().orElse(null);
  }
}
