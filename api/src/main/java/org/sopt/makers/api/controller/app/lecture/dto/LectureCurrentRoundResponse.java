package org.sopt.makers.api.controller.app.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.admin.app.AppSubLecture;

public record LectureCurrentRoundResponse(
    @Schema(description = "세부 세션 아이디. 출석 체크 요청에 그대로 넣는다", example = "1") Long id,
    @Schema(description = "현재 출석 차수", example = "1") int round) {

  public static LectureCurrentRoundResponse from(AppSubLecture subLecture) {
    return new LectureCurrentRoundResponse(subLecture.id(), subLecture.round());
  }
}
