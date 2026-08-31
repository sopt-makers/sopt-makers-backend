package org.sopt.makers.domain.crew.slack.port;

public interface SlackMessageSenderPort {

  void sendThreadMessage(String channelId, String threadTimestamp, String message);
}
