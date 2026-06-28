package org.sopt.makers.domain.admin.app;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;
import org.sopt.makers.domain.admin.attendance.LectureStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

public record AppLecture(
    Long id,
    String location,
    String name,
    LocalDateTime startAt,
    LocalDateTime endAt,
    LectureAttribute attribute,
    LectureStatus status,
    List<SubAttendance> subAttendances) {

  public boolean isBefore() {
    return status == LectureStatus.BEFORE;
  }

  public boolean isFirst() {
    return status == LectureStatus.FIRST;
  }

  public boolean isEnd() {
    return status == LectureStatus.END;
  }
}
