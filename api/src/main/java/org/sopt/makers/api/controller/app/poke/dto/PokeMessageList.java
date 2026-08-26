package org.sopt.makers.api.controller.app.poke.dto;

import java.util.List;

public record PokeMessageList(String header, List<PokeMessage> messages) {

  public static PokeMessageList of(
      String header, List<org.sopt.makers.domain.app.poke.PokeMessage> messages) {
    return new PokeMessageList(header, messages.stream().map(PokeMessage::of).toList());
  }

  public record PokeMessage(Long messageId, String content) {

    private static PokeMessage of(org.sopt.makers.domain.app.poke.PokeMessage message) {
      return new PokeMessage(message.id(), message.content());
    }
  }
}
