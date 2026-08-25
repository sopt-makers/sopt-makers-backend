package org.sopt.makers.domain.crew.notification;

import java.time.LocalDateTime;
import java.time.LocalTime;

public final class MeetingNotificationTimePolicy {

  private static final LocalTime START_TIME = LocalTime.of(8, 0);
  private static final LocalTime END_TIME = LocalTime.of(22, 0);

  private MeetingNotificationTimePolicy() {}

  public static boolean isPublishable(LocalDateTime now) {
    LocalTime time = now.toLocalTime();
    return !time.isBefore(START_TIME) && !time.isAfter(END_TIME);
  }
}
