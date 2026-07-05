package org.sopt.makers.api.controller.admin.lecture.dto;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AdminLecture;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;
import org.sopt.makers.domain.admin.attendance.service.AdminLectureService;

public record LecturesGetResponse(int generation, List<LectureItem> lectures) {

  public static LecturesGetResponse from(
      int generation, List<AdminLecture> lectures, AdminLectureService service) {
    return new LecturesGetResponse(
        generation, lectures.stream().map(l -> LectureItem.from(l, service)).toList());
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
      AttendanceStatusSummaryVo attendances) {

    public static LectureItem from(AdminLecture lecture, AdminLectureService service) {
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
          AttendanceStatusSummaryVo.from(lecture, service));
    }
  }
}
