package org.sopt.makers.api.controller.admin.lecture.dto;

import java.util.List;
import java.util.Map;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.AttendanceStatusSummary;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;

public record LecturesGetResponse(int generation, List<LectureItem> lectures) {

  public static LecturesGetResponse from(
      int generation, List<Lecture> lectures, Map<Long, AttendanceStatusSummary> summaries) {
    return new LecturesGetResponse(
        generation,
        lectures.stream().map(l -> LectureItem.from(l, summaries.get(l.id()))).toList());
  }

  public record LectureItem(
      Long lectureId,
      String name,
      Part partValue,
      String partName,
      String startDate,
      String endDate,
      LectureAttribute attributeValue,
      String attributeName,
      String place,
      AttendanceStatusSummary attendances) {

    public static LectureItem from(Lecture lecture, AttendanceStatusSummary summary) {
      return new LectureItem(
          lecture.id(),
          lecture.name(),
          lecture.part(),
          lecture.part().getName(),
          lecture.startDate().toString(),
          lecture.endDate().toString(),
          lecture.attribute(),
          lecture.attribute().getName(),
          lecture.place(),
          summary);
    }
  }
}
