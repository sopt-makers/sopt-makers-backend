package org.sopt.makers.api.controller.admin.lecture.dto;

import org.sopt.makers.domain.admin.lecture.Lecture;

public record LectureCreateResponse(Long lectureId) {

  public static LectureCreateResponse from(Lecture lecture) {
    return new LectureCreateResponse(lecture.id());
  }
}
