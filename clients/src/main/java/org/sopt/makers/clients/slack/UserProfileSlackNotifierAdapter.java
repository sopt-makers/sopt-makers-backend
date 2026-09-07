package org.sopt.makers.clients.slack;

import com.slack.api.Slack;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.member.profile.port.UserProfileNotifierPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserProfileSlackNotifierAdapter implements UserProfileNotifierPort {

  private final SlackProperties properties;
  private final String activeProfile;

  public UserProfileSlackNotifierAdapter(
      SlackProperties properties, @Value("${spring.profiles.active:}") String activeProfile) {
    this.properties = properties;
    this.activeProfile = activeProfile;
  }

  @Override
  public void notifyNewProfile(Long userId, String name, String idealType) {
    if (!"prod".equals(activeProfile)) {
      return;
    }

    String channelId = properties.newProfileChannelId();
    if (channelId == null || channelId.isBlank()) {
      return;
    }

    try {
      ChatPostMessageResponse response =
          Slack.getInstance()
              .methods(properties.botToken())
              .chatPostMessage(
                  request -> request.channel(channelId).text(buildMessage(userId, name, idealType)));
      if (!response.isOk()) {
        log.error("신규 프로필 슬랙 메시지 전송 실패: {}", response.getError());
      }
    } catch (Exception exception) {
      log.error("신규 프로필 슬랙 메시지 전송 실패", exception);
    }
  }

  private String buildMessage(Long userId, String name, String idealType) {
    return "새로운 유저가 프로필을 만들었어요!\n"
        + "*이름:*\n"
        + name
        + "\n*프로필링크:*\n<https://playground.sopt.org/members/"
        + userId
        + "|멤버프로필>\n*이상형:*\n"
        + idealType;
  }
}
