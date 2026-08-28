package org.sopt.makers.clients.slack;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.slack")
public record SlackProperties(String botToken, String appToken, boolean socketModeEnabled) {

  public void validateSocketModeConfiguration() {
    if (!socketModeEnabled) {
      return;
    }
    if (botToken == null || botToken.isBlank() || appToken == null || appToken.isBlank()) {
      throw new IllegalStateException(
          "Slack Socket Mode가 활성화되어 있지만 SLACK_BOT_TOKEN 또는 SLACK_APP_TOKEN이 비어 있습니다.");
    }
  }
}
