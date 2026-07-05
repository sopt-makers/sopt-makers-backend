package org.sopt.makers.domain.admin.lecture;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.core.type.Part;

public record Lecture(
    Long id,
    String name,
    Part part,
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

  public boolean isFirst() {
    return status == LectureStatus.FIRST;
  }

  public boolean isSecond() {
    return status == LectureStatus.SECOND;
  }

  public boolean isNotYetToEnd() {
    return endDate.isAfter(LocalDateTime.now());
  }
}
