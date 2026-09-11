package org.sopt.makers.api.controller.app.fortune.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.fortune.facade.FortuneFacade.TodayFortuneWord;

public record FortuneResponse(
    @Schema(description = "유저 이름", example = "김솝트") String userName,
    @Schema(description = "오늘의 솝마디", example = "오늘은 새로운 도전을 시작하기 좋은 날이에요") String title) {

  public static FortuneResponse of(TodayFortuneWord todayFortuneWord) {
    return new FortuneResponse(todayFortuneWord.userName(), todayFortuneWord.title());
  }
}
