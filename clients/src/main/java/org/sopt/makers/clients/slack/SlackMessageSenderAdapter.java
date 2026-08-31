package org.sopt.makers.clients.slack;

import static org.sopt.makers.domain.crew.slack.exception.SlackEmojiFailure.FAIL_SEND_SLACK_MESSAGE;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import java.io.IOException;
import org.sopt.makers.domain.crew.slack.exception.SlackEmojiException;
import org.sopt.makers.domain.crew.slack.port.SlackMessageSenderPort;
import org.springframework.stereotype.Component;

@Component
public class SlackMessageSenderAdapter implements SlackMessageSenderPort {

  private final SlackProperties properties;

  public SlackMessageSenderAdapter(SlackProperties properties) {
    this.properties = properties;
  }

  @Override
  public void sendThreadMessage(String channelId, String threadTimestamp, String message) {
    try {
      ChatPostMessageResponse response =
          Slack.getInstance()
              .methods(properties.botToken())
              .chatPostMessage(
                  request -> request.channel(channelId).threadTs(threadTimestamp).text(message));
      if (!response.isOk()) {
        throw new SlackEmojiException(FAIL_SEND_SLACK_MESSAGE);
      }
    } catch (IOException | SlackApiException exception) {
      throw new SlackEmojiException(FAIL_SEND_SLACK_MESSAGE);
    }
  }
}
