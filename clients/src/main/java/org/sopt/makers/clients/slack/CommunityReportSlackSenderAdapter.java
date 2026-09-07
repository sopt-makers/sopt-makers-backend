package org.sopt.makers.clients.slack;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import java.io.IOException;
import org.sopt.makers.domain.playground.community.notification.port.CommunitySlackNotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommunityReportSlackSenderAdapter implements CommunitySlackNotificationPort {

  private final SlackProperties properties;
  private final String activeProfile;

  public CommunityReportSlackSenderAdapter(
      SlackProperties properties, @Value("${spring.profiles.active:}") String activeProfile) {
    this.properties = properties;
    this.activeProfile = activeProfile;
  }

  @Override
  public void sendReportMessage(String message) {
    send(properties.communityReportChannelId(), message, "커뮤니티 신고 슬랙 메시지 전송 실패");
  }

  @Override
  public void sendNotMakersMessage(String message) {
    send(properties.communityNotMakersChannelId(), message, "비 메이커스 게시글 작성 슬랙 메시지 전송 실패");
  }

  private void send(String channelId, String message, String failureMessage) {
    if (!"prod".equals(activeProfile)) {
      return;
    }

    if (channelId == null || channelId.isBlank()) {
      return;
    }

    try {
      ChatPostMessageResponse response =
          Slack.getInstance().methods(properties.botToken()).chatPostMessage(request -> request.channel(channelId).text(message));
      if (!response.isOk()) {
        throw new IllegalStateException("Slack API 응답 실패: " + response.getError());
      }
    } catch (IOException | SlackApiException exception) {
      throw new IllegalStateException(failureMessage, exception);
    }
  }
}
