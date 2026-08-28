package org.sopt.makers.clients.sms;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.enums.ChatCategory;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatSmsPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoffeeChatSmsAdapter implements CoffeeChatSmsPort {

  private static final String PROFILE_URL_FORMAT = "https://playground.sopt.org/members/%d";
  private static final String MESSAGE_FORMAT =
      "[SOPT Playground] %s(%s)님에게 커피챗 신청이 왔어요!\n\n카테고리: %s\n내용: %s\n\n신청자 프로필 확인: "
          + PROFILE_URL_FORMAT;

  private final GabiaClient gabiaClient;

  @Override
  public void send(
      String senderName,
      String senderPart,
      ChatCategory category,
      String content,
      Long senderId,
      String senderPhone,
      String receiverPhone) {
    String message =
        String.format(
            MESSAGE_FORMAT, senderName, senderPart, category.name(), content, senderId);
    gabiaClient.send(receiverPhone, message);
  }
}
