package org.sopt.makers.api.controller.admin.lecture.dto;

import org.sopt.makers.domain.admin.lecture.SubLecture;

public record SubLectureStartResponse(Long lectureId, Long subLectureId) {

  public static SubLectureStartResponse from(Long lectureId, SubLecture subLecture) {
    return new SubLectureStartResponse(lectureId, subLecture.id());
  }
}
