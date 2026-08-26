package org.sopt.makers.domain.crew.meeting.demand;

import java.time.LocalDateTime;

public record MeetingDemandWaitHistory(
    Long id, Long meetingDemandId, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static MeetingDemandWaitHistory create(Long meetingDemandId, Long userId) {
    return new MeetingDemandWaitHistory(null, meetingDemandId, userId, null, null);
  }
}
