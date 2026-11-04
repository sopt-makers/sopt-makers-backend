package org.sopt.makers.api.controller.playground.wordchaingame.dto;

import org.sopt.makers.domain.playground.wordchaingame.service.WordChainGameService.WinnerResult;

public record WordChainGameWinnerResponse(Long roomId, UserResponse winner) {

  public static WordChainGameWinnerResponse from(WinnerResult result) {
    return new WordChainGameWinnerResponse(result.roomId(), UserResponse.from(result.winner()));
  }
}
