package org.sopt.makers.clients.push.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;
import org.sopt.makers.domain.app.push.PushMessage;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PushSendRequest(
    Set<String> userIds,
    String title,
    String content,
    String category,
    String deepLink,
    String webLink) {

  public static PushSendRequest from(PushMessage message) {
    return new PushSendRequest(
        message.userIds().stream()
            .map(String::valueOf)
            .collect(java.util.stream.Collectors.toSet()),
        message.title(),
        message.content(),
        message.category().name(),
        message.deepLink(),
        message.webLink());
  }
}
