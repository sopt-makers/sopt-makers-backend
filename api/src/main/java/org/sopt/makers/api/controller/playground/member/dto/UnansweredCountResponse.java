package org.sopt.makers.api.controller.playground.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UnansweredCountResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "답변 대기 중인 질문 개수") Long count) {

  public static UnansweredCountResponse from(long count) {
    return new UnansweredCountResponse(count);
  }
}
