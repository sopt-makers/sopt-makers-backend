package org.sopt.makers.api.controller.admin.lecture.dto;

import org.sopt.makers.domain.admin.lecture.Lecture;

public record LectureDetailResponse(
    Long lectureId,
    String part,
    String name,
    String place,
    String attribute,
    String startDate,
    String endDate,
    int generation) {

  public static LectureDetailResponse from(Lecture lecture) {
    return new LectureDetailResponse(
        lecture.id(),
        lecture.part().getName(),
        lecture.name(),
        lecture.place(),
        lecture.attribute().getName(),
        lecture.startDate().toString(),
        lecture.endDate().toString(),
        lecture.generation());
  }
}
