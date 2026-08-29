package org.sopt.makers.clients.slack;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.ReactionAddedEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.crew.slack.service.SlackEmojiService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "external.slack",
    name = "socket-mode-enabled",
    havingValue = "true")
public class SlackSocketModeLifecycle {

  private final SlackProperties properties;
  private final SlackEmojiService slackEmojiService;

  private SocketModeApp socketModeApp;

  @PostConstruct
  public void start() {
    properties.validateSocketModeConfiguration();
    try {
      App app = new App(AppConfig.builder().singleTeamBotToken(properties.botToken()).build());
      app.event(
          ReactionAddedEvent.class,
          (payload, context) -> {
            ReactionAddedEvent event = payload.getEvent();
            try {
              boolean handled =
                  slackEmojiService.handleReaction(
                      event.getReaction(),
                      event.getUser(),
                      event.getItem().getChannel(),
                      event.getItem().getTs());
              if (handled) {
                log.info(
                    "Slack 호출 이모지 처리 완료 - emoji={}, channel={}",
                    event.getReaction(),
                    event.getItem().getChannel());
              }
            } catch (RuntimeException exception) {
              log.warn(
                  "Slack 호출 이모지 처리 실패 - emoji={}, channel={}",
                  event.getReaction(),
                  event.getItem().getChannel(),
                  exception);
            }
            return context.ack();
          });
      socketModeApp = new SocketModeApp(properties.appToken(), app);
      socketModeApp.startAsync();
      log.info("Slack Socket Mode 시작 완료");
    } catch (Exception exception) {
      throw new IllegalStateException("Slack Socket Mode 시작에 실패했습니다.", exception);
    }
  }

  @PreDestroy
  public void stop() {
    if (socketModeApp == null) {
      return;
    }
    try {
      socketModeApp.stop();
      log.info("Slack Socket Mode 종료 완료");
    } catch (Exception exception) {
      log.warn("Slack Socket Mode 종료에 실패했습니다.", exception);
    }
  }
}
