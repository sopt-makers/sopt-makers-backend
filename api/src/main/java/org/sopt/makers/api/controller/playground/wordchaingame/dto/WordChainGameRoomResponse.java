package org.sopt.makers.api.controller.playground.wordchaingame.dto;

import java.util.List;
import org.sopt.makers.domain.playground.wordchaingame.service.WordChainGameService.RoomResult;

public record WordChainGameRoomResponse(
    Long roomId, String startWord, UserResponse startUser, List<WordResponse> words) {

  public record WordResponse(String word, UserResponse user) {}

  public static WordChainGameRoomResponse from(RoomResult result) {
    List<WordResponse> wordResponses =
        result.words().stream()
            .map(w -> new WordResponse(w.word(), UserResponse.from(w.user())))
            .toList();
    return new WordChainGameRoomResponse(
        result.id(), result.startWord(), UserResponse.from(result.startUser()), wordResponses);
  }
}
