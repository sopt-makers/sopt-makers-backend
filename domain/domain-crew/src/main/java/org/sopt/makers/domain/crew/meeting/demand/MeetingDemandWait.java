package org.sopt.makers.domain.crew.meeting.demand;

import java.time.LocalDateTime;

public record MeetingDemandWait(
    Long id, Long meetingDemandId, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static MeetingDemandWait create(Long meetingDemandId, Long userId) {
    return new MeetingDemandWait(null, meetingDemandId, userId, null, null);
  }
}
