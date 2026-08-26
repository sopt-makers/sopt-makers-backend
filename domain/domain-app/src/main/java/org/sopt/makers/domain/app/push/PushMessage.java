package org.sopt.makers.domain.app.push;

import java.util.Set;
import org.sopt.makers.domain.app.notification.NotificationCategory;

public record PushMessage(
    Set<Long> userIds,
    String title,
    String content,
    NotificationCategory category,
    String deepLink,
    String webLink) {

  public PushMessage {
    userIds = userIds == null ? Set.of() : Set.copyOf(userIds);
  }
}
