package org.sopt.makers.domain.app.poke.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.PokeMessage;
import org.sopt.makers.domain.app.poke.PokeMessageType;
import org.sopt.makers.domain.app.poke.port.PokeMessageRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokeMessageService {

  private static final int MESSAGES_QUANTITY_AT_ONCE = 4;
  private static final String MESSAGES_HEADER_FOR_POKE = "함께 보낼 메시지를 선택해주세요";
  private static final String MESSAGES_HEADER_FOR_REPLY = "답장하고 싶은 메시지를 선택해주세요";
  // TODO : 추후 고정 메세지 db에서 관리하도록 변경
  private static final String FIXED_MESSAGE = "콕 \uD83D\uDC48";

  private final PokeMessageRepositoryPort pokeMessageRepositoryPort;

  public String getMessagesHeaderComment(String type) {
    if (PokeMessageType.ofParam(type) == PokeMessageType.REPLY_NEW) {
      return MESSAGES_HEADER_FOR_REPLY;
    }
    return MESSAGES_HEADER_FOR_POKE;
  }

  public List<PokeMessage> pickRandomMessageByTypeOf(String type) {
    List<PokeMessage> messages =
        new ArrayList<>(pokeMessageRepositoryPort.findAllByType(PokeMessageType.ofParam(type)));
    Collections.shuffle(messages);
    return messages.stream().limit(MESSAGES_QUANTITY_AT_ONCE).toList();
  }

  public PokeMessage getFixedMessage() {
    return new PokeMessage(0L, FIXED_MESSAGE, PokeMessageType.POKE_ALL);
  }
}
