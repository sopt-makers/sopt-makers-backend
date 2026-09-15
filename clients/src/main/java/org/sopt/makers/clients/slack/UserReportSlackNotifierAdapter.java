package org.sopt.makers.clients.slack;

import com.slack.api.Slack;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.member.relation.port.UserReportNotifierPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserReportSlackNotifierAdapter implements UserReportNotifierPort {

  private final SlackProperties properties;
  private final String activeProfile;

  public UserReportSlackNotifierAdapter(
      SlackProperties properties, @Value("${spring.profiles.active:}") String activeProfile) {
    this.properties = properties;
    this.activeProfile = activeProfile;
  }

  @Override
  public void notifyUserReport(Long reporterUserId, Long reportedUserId) {
    if (!"prod".equals(activeProfile)) {
      return;
    }

    String channelId = properties.memberReportChannelId();
    if (channelId == null || channelId.isBlank()) {
      return;
    }

    try {
      ChatPostMessageResponse response =
          Slack.getInstance()
              .methods(properties.botToken())
              .chatPostMessage(
                  request ->
                      request
                          .channel(channelId)
                          .text(buildReportMessage(reporterUserId, reportedUserId)));
      if (!response.isOk()) {
        log.error("유저 신고 슬랙 메시지 전송 실패: {}", response.getError());
      }
    } catch (Exception exception) {
      log.error("유저 신고 슬랙 메시지 전송 실패", exception);
    }
  }

  private String buildReportMessage(Long reporterUserId, Long reportedUserId) {
    return "🚨유저 신고 발생!🚨\n"
        + "신고자 ID: "
        + reporterUserId
        + "\n"
        + "신고 당한 유저 ID: "
        + reportedUserId
        + " (https://playground.sopt.org/members/"
        + reportedUserId
        + ")";
  }
}
