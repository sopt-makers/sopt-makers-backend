package org.sopt.makers.domain.admin.attendance;

import java.time.LocalDateTime;
import java.util.List;

public record AdminLecture(
    Long id,
    String name,
    org.sopt.makers.core.type.Part part,
    int generation,
    String place,
    LocalDateTime startDate,
    LocalDateTime endDate,
    LectureAttribute attribute,
    LectureStatus status,
    List<SubLecture> subLectures) {

  public boolean isEnd() {
    return status == LectureStatus.END;
  }

  public boolean isBefore() {
    return status == LectureStatus.BEFORE;
  }

  public boolean isNotYetToEnd() {
    return endDate.isAfter(LocalDateTime.now());
  }
}
