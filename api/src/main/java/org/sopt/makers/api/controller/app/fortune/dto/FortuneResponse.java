package org.sopt.makers.api.controller.app.fortune.dto;

import org.sopt.makers.domain.app.fortune.facade.FortuneFacade.TodayFortuneWord;

public record FortuneResponse(String userName, String title) {

  public static FortuneResponse of(TodayFortuneWord todayFortuneWord) {
    return new FortuneResponse(todayFortuneWord.userName(), todayFortuneWord.title());
  }
}
