package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;

public record OpenedMeetingResponse(
    Long meetingId, String title, String imageUrl, String category, CreatorResponse user) {

  public static OpenedMeetingResponse from(MeetingDemandService.OpenedMeeting openedMeeting) {
    Meeting meeting = openedMeeting.meeting();
    return new OpenedMeetingResponse(
        meeting.id(),
        meeting.title(),
        meeting.images().stream().findFirst().map(MeetingImage::url).orElse(null),
        meeting.category().getValue(),
        CreatorResponse.from(openedMeeting.creator()));
  }

  public record CreatorResponse(Long id, String name, String profileImage) {

    public static CreatorResponse from(MeetingUser user) {
      return user == null ? null : new CreatorResponse(user.id(), user.name(), user.profileImage());
    }
  }
}
