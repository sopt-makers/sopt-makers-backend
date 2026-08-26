package org.sopt.makers.api.controller.internal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.notification.NotificationType;
import org.sopt.makers.domain.app.notification.RegisterNotificationCommand;

public class InternalNotificationRequest {

  private InternalNotificationRequest() {}

  public record Register(
      List<String> userIds,
      @NotNull String title,
      String content,
      @NotNull NotificationType type,
      @NotNull NotificationCategory category,
      String deepLink,
      String webLink,
      @JsonProperty("id") String notificationId) {

    public RegisterNotificationCommand toCommand() {
      return new RegisterNotificationCommand(
          userIds == null ? List.of() : userIds.stream().map(Long::parseLong).toList(),
          notificationId,
          title,
          content,
          type,
          category,
          deepLink,
          webLink);
    }
  }
}
