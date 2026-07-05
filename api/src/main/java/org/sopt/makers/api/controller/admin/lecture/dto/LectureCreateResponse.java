package org.sopt.makers.api.controller.admin.lecture.dto;

import org.sopt.makers.domain.admin.attendance.AdminLecture;

public record LectureCreateResponse(Long lectureId) {

  public static LectureCreateResponse from(AdminLecture lecture) {
    return new LectureCreateResponse(lecture.id());
  }
}
