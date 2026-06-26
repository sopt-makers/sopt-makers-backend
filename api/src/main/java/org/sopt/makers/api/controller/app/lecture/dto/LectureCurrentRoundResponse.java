package org.sopt.makers.api.controller.app.lecture.dto;

import org.sopt.makers.domain.admin.app.AppSubLecture;

public record LectureCurrentRoundResponse(Long id, int round) {

  public static LectureCurrentRoundResponse from(AppSubLecture subLecture) {
    return new LectureCurrentRoundResponse(subLecture.id(), subLecture.round());
  }
}
