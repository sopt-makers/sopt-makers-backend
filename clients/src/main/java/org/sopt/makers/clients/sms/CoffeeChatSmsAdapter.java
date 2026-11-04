package org.sopt.makers.clients.sms;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.enums.ChatCategory;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatSmsPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoffeeChatSmsAdapter implements CoffeeChatSmsPort {

  private static final String MESSAGE_TITLE = "[SOPT makers] 쪽지가 도착했어요!";
  private static final String PROFILE_URL_FORMAT = "https://playground.sopt.org/members/%d";
  private static final String TOPIC_COFFEE_CHAT = "커피챗";
  private static final String TOPIC_NOTE = "친목";
  private static final String GUIDE_MESSAGE =
      "나의 조언이 용기 낸 SOPT 동료에게 큰 도움이 될 수 있어요. \n" + "쪽지에 응하신다면 아래 동료의 전화번호로 직접 연락해 주세요.";

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
    String topic = category == ChatCategory.COFFEE_CHAT ? TOPIC_COFFEE_CHAT : TOPIC_NOTE;
    String message =
        MESSAGE_TITLE
            + "\n\n"
            + "[이름] "
            + senderName
            + "\n"
            + "[파트] "
            + senderPart
            + "\n"
            + "[주제] "
            + topic
            + "\n"
            + "[이런 내용이 궁금해요]\n"
            + content
            + "\n\n"
            + "[멤버 프로필 링크]\n"
            + String.format(PROFILE_URL_FORMAT, senderId)
            + "\n\n"
            + GUIDE_MESSAGE
            + "\n\n"
            + "[연락처] "
            + senderPhone;
    gabiaClient.send(receiverPhone, message);
  }
}
