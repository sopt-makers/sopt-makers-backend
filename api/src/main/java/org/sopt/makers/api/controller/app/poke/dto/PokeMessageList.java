package org.sopt.makers.api.controller.app.poke.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PokeMessageList(
    @Schema(description = "메시지 목록 위에 띄울 문구", example = "친구를 찔러보세요") String header,
    @Schema(description = "고를 수 있는 찌르기 메시지 목록") List<PokeMessage> messages) {

  public static PokeMessageList of(
      String header, List<org.sopt.makers.domain.app.poke.PokeMessage> messages) {
    return new PokeMessageList(header, messages.stream().map(PokeMessage::of).toList());
  }

  public record PokeMessage(
      @Schema(description = "메시지 아이디", example = "91") Long messageId,
      @Schema(description = "메시지 내용", example = "안녕 친구야") String content) {

    private static PokeMessage of(org.sopt.makers.domain.app.poke.PokeMessage message) {
      return new PokeMessage(message.id(), message.content());
    }
  }
}
