package org.sopt.makers.domain.admin.attendance;

import java.time.LocalDateTime;
import java.util.Objects;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;

public record SubLecture(
    Long id,
    Long lectureId,
    LectureAttribute attribute,
    int generation,
    int round,
    LocalDateTime startAt,
    String code) {

  private static final int ATTENDANCE_WINDOW_MINUTES = 10;

  public void validateForAttendance(String inputCode) {
    LocalDateTime now = LocalDateTime.now();

    if (isNotStarted(now)) {
      throw new AttendanceException(AttendanceFailure.ATTENDANCE_NOT_STARTED);
    }
    if (isEnded(now)) {
      throw new AttendanceException(AttendanceFailure.ATTENDANCE_ENDED);
    }
    if (!isMatchCode(inputCode)) {
      throw new AttendanceException(AttendanceFailure.INVALID_ATTENDANCE_CODE);
    }
  }

  private boolean isNotStarted(LocalDateTime now) {
    return startAt == null || startAt.isAfter(now);
  }

  private boolean isEnded(LocalDateTime now) {
    return startAt != null && now.isAfter(startAt.plusMinutes(ATTENDANCE_WINDOW_MINUTES));
  }

  private boolean isMatchCode(String inputCode) {
    return Objects.equals(this.code, inputCode);
  }
}
