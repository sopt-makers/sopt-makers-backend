package org.sopt.makers.domain.crew.notification;

import java.util.List;

public record MeetingNotification(
    List<Long> userIds, String title, String content, String category, Long meetingId) {

  public MeetingNotification {
    userIds = userIds == null ? List.of() : List.copyOf(userIds);
  }
}
