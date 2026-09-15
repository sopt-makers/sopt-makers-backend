package org.sopt.makers.api.controller.crew.meeting.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;
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
    int status,
    List<String> welcomeMessageTypes,
    List<String> meetingKeywordTypes) {

  public static MeetingSummaryResponse from(MeetingFacade.MeetingSummaryResult result) {
    MeetingService.MeetingSummary summary = result.meetingSummary();
    Meeting meeting = summary.meeting();
    return new MeetingSummaryResponse(
        meeting.id(),
        summary.leader().userId(),
        meeting.title(),
        meeting.subTitle(),
        meeting.category().getValue(),
        meeting.images(),
        meeting.startDate(),
        meeting.endDate(),
        meeting.capacity(),
        summary.appliedCount(),
        summary.approvedCount(),
        summary.status().getValue(),
        MeetingTagMapper.fromWelcomeMessageTypes(result.meetingTagInfo().welcomeMessageTypes()),
        MeetingTagMapper.fromMeetingKeywordTypes(result.meetingTagInfo().meetingKeywordTypes()));
  }
}
