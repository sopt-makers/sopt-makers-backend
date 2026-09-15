package org.sopt.makers.domain.app.poke.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.poke.exception.PokeFailure.NOT_FOUND_POKE_MESSAGE_TYPE;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.poke.PokeMessage;
import org.sopt.makers.domain.app.poke.PokeMessageType;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.port.PokeMessageRepositoryPort;

@DisplayName("PokeMessageService 테스트")
class PokeMessageServiceTest {

  private final Map<PokeMessageType, List<PokeMessage>> messagesByType =
      new EnumMap<>(PokeMessageType.class);

  private PokeMessageService pokeMessageService;

  @BeforeEach
  void setUp() {
    messagesByType.clear();
    PokeMessageRepositoryPort repository = type -> messagesByType.getOrDefault(type, List.of());
    pokeMessageService = new PokeMessageService(repository);
  }

  private void seedMessages(PokeMessageType type, int count) {
    List<PokeMessage> messages = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      messages.add(new PokeMessage((long) i, type.getParameter() + i, type));
    }
    messagesByType.put(type, messages);
  }

  @Test
  @DisplayName("답장 타입이면 답장용 헤더 문구를 준다")
  void headerCommentForReply() {
    assertThat(pokeMessageService.getMessagesHeaderComment("replyNew"))
        .isEqualTo("답장하고 싶은 메시지를 선택해주세요");
  }

  @Test
  @DisplayName("답장이 아닌 타입이면 찌르기용 헤더 문구를 준다")
  void headerCommentForPoke() {
    assertThat(pokeMessageService.getMessagesHeaderComment("pokeSomeone"))
        .isEqualTo("함께 보낼 메시지를 선택해주세요");
    assertThat(pokeMessageService.getMessagesHeaderComment("pokeFriend"))
        .isEqualTo("함께 보낼 메시지를 선택해주세요");
    assertThat(pokeMessageService.getMessagesHeaderComment("pokeAll"))
        .isEqualTo("함께 보낼 메시지를 선택해주세요");
  }

  @Test
  @DisplayName("없는 메시지 타입이면 NOT_FOUND_POKE_MESSAGE_TYPE 예외가 발생한다")
  void headerCommentThrowsWhenTypeUnknown() {
    assertThatThrownBy(() -> pokeMessageService.getMessagesHeaderComment("unknown"))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_POKE_MESSAGE_TYPE);
  }

  @Test
  @DisplayName("메시지는 한 번에 최대 4개까지 뽑는다")
  void picksAtMostFourMessages() {
    seedMessages(PokeMessageType.POKE_SOMEONE, 6);

    List<PokeMessage> picked = pokeMessageService.pickRandomMessageByTypeOf("pokeSomeone");

    assertThat(picked).hasSize(4).doesNotHaveDuplicates();
    assertThat(picked).allMatch(message -> message.type() == PokeMessageType.POKE_SOMEONE);
  }

  @Test
  @DisplayName("메시지가 4개보다 적으면 있는 만큼만 뽑는다")
  void picksAllWhenFewerThanFour() {
    seedMessages(PokeMessageType.REPLY_NEW, 2);

    assertThat(pokeMessageService.pickRandomMessageByTypeOf("replyNew")).hasSize(2);
  }

  @Test
  @DisplayName("고정 메시지는 id 0의 콕 메시지이며 모든 상황용 타입이다")
  void fixedMessage() {
    PokeMessage fixedMessage = pokeMessageService.getFixedMessage();

    assertThat(fixedMessage.id()).isZero();
    assertThat(fixedMessage.content()).isEqualTo("콕 👈");
    assertThat(fixedMessage.type()).isEqualTo(PokeMessageType.POKE_ALL);
  }
}
