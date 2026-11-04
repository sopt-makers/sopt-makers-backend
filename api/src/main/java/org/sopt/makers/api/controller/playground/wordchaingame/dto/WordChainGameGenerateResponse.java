package org.sopt.makers.api.controller.playground.wordchaingame.dto;

import org.sopt.makers.domain.playground.wordchaingame.service.WordChainGameService.CreateRoomResult;
import org.sopt.makers.domain.playground.wordchaingame.service.WordChainGameService.CreateWordResult;

public record WordChainGameGenerateResponse(Long roomId, String word, UserResponse user) {

  public static WordChainGameGenerateResponse from(CreateWordResult result) {
    return new WordChainGameGenerateResponse(
        result.roomId(), result.word(), UserResponse.from(result.userInfo()));
  }

  public static WordChainGameGenerateResponse from(CreateRoomResult result) {
    return new WordChainGameGenerateResponse(
        result.roomId(), result.startWord(), UserResponse.from(result.creatorInfo()));
  }
}
