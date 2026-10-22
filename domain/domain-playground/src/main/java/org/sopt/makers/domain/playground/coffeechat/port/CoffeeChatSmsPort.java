package org.sopt.makers.domain.playground.coffeechat.port;

import org.sopt.makers.domain.playground.coffeechat.enums.ChatCategory;

public interface CoffeeChatSmsPort {

  void send(
      String senderName,
      String senderPart,
      ChatCategory category,
      String content,
      Long senderId,
      String senderPhone,
      String receiverPhone);
}
