package org.sopt.makers.domain.crew.meeting.demand;

import java.time.LocalDateTime;

public record MeetingDemandOpenedNotification(
    Long id,
    Long meetingId,
    LocalDateTime sentAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static MeetingDemandOpenedNotification create(Long meetingId) {
    return new MeetingDemandOpenedNotification(null, meetingId, null, null, null);
  }

  public boolean isSent() {
    return sentAt != null;
  }

  public MeetingDemandOpenedNotification markSent(LocalDateTime sentTime) {
    return new MeetingDemandOpenedNotification(id, meetingId, sentTime, createdAt, updatedAt);
  }
}
