package org.sopt.makers.clients.slack;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import java.io.IOException;
import org.sopt.makers.domain.playground.community.notification.port.CommunitySlackNotificationPort;
import org.springframework.stereotype.Component;

@Component
public class CommunityReportSlackSenderAdapter implements CommunitySlackNotificationPort {

  private final SlackProperties properties;

  public CommunityReportSlackSenderAdapter(SlackProperties properties) {
    this.properties = properties;
  }

  @Override
  public void sendReportMessage(String message) {
    String channelId = properties.communityReportChannelId();
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
      throw new IllegalStateException("커뮤니티 신고 슬랙 메시지 전송 실패", exception);
    }
  }
}
