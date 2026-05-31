package org.sopt.makers.domain.admin.attendance;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;

@Getter
@AllArgsConstructor
public class SubLecture {

  private static final int ATTENDANCE_WINDOW_MINUTES = 10;

  private final Long id;
  private final Long lectureId;
  private final LectureAttribute attribute;
  private final int generation;
  private final int round;
  private final LocalDateTime startAt;
  private final String code;

  public void validateForAttendance(String inputCode) {
    if (isNotStarted()) {
      throw new AttendanceException(AttendanceFailure.ATTENDANCE_NOT_STARTED);
    }
    if (isEnded()) {
      throw new AttendanceException(AttendanceFailure.ATTENDANCE_ENDED);
    }
    if (!isMatchCode(inputCode)) {
      throw new AttendanceException(AttendanceFailure.INVALID_ATTENDANCE_CODE);
    }
  }

  private boolean isNotStarted() {
    return startAt == null || startAt.isAfter(LocalDateTime.now());
  }

  private boolean isEnded() {
    return LocalDateTime.now().isAfter(startAt.plusMinutes(ATTENDANCE_WINDOW_MINUTES));
  }

  private boolean isMatchCode(String inputCode) {
    return this.code.equals(inputCode);
  }
}
