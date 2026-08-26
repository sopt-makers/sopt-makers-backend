package org.sopt.makers.domain.crew.meeting.demand.notification;

import java.util.List;

public record MeetingDemandNotification(
    List<Long> userIds, String title, String content, String category, String webPath) {

  public MeetingDemandNotification {
    userIds = userIds == null ? List.of() : List.copyOf(userIds);
  }
}
