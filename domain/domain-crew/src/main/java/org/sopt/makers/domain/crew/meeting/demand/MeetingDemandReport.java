package org.sopt.makers.domain.crew.meeting.demand;

import java.time.LocalDateTime;

public record MeetingDemandReport(
    Long id,
    Long userId,
    MeetingDemandReportTarget targetType,
    Long targetId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static MeetingDemandReport demand(Long userId, Long meetingDemandId) {
    return new MeetingDemandReport(
        null, userId, MeetingDemandReportTarget.DEMAND, meetingDemandId, null, null);
  }

  public static MeetingDemandReport comment(Long userId, Long commentId) {
    return new MeetingDemandReport(
        null, userId, MeetingDemandReportTarget.COMMENT, commentId, null, null);
  }
}
