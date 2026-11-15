package org.sopt.makers.api.controller.app.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberScoreResponse(
    @Schema(description = "현재 기수 출석 점수. 값이 없으면 기본 점수 2점", example = "2.0") float score) {

  public static MemberScoreResponse from(float score) {
    return new MemberScoreResponse(score);
  }
}
