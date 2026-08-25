package org.sopt.makers.api.controller.crew.meeting.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record MeetingSummaryResponse(
    Long id,
    Long userId,
    String title,
    String subTitle,
    String category,
    List<MeetingImage> imageURL,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer capacity,
    long appliedCount,
    long approvedCount,
    int status) {

  public static MeetingSummaryResponse from(MeetingService.MeetingSummary summary) {
    Meeting meeting = summary.meeting();
    return new MeetingSummaryResponse(
        meeting.id(),
        meeting.userId(),
        meeting.title(),
        meeting.subTitle(),
        meeting.category().getValue(),
        meeting.images(),
        meeting.startDate(),
        meeting.endDate(),
        meeting.capacity(),
        summary.appliedCount(),
        summary.approvedCount(),
        summary.status().getValue());
  }
}
