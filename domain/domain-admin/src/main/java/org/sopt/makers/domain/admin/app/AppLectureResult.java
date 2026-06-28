package org.sopt.makers.domain.admin.app;

import java.util.List;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

public record AppLectureResult(
    AppLectureResponseType type,
    AppLecture lecture,
    String message,
    List<SubAttendance> attendances) {

  public static AppLectureResult empty() {
    return new AppLectureResult(AppLectureResponseType.NO_SESSION, null, "", List.of());
  }
}
