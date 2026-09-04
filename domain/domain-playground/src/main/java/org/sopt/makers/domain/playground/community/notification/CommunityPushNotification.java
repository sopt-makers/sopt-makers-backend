package org.sopt.makers.domain.playground.community.notification;

import java.util.List;

public record CommunityPushNotification(List<Long> recipientIds, String title, String content, String webLink) {

  public CommunityPushNotification {
    recipientIds = recipientIds == null ? List.of() : List.copyOf(recipientIds);
  }
}
