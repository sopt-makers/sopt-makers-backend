package org.sopt.makers.api.controller.crew.post.dto;

import java.util.List;
import org.sopt.makers.domain.playground.post.MeetingPostContext;

public record PostMeetingResponse(
    Long id, String title, String category, List<PostMeetingImageResponse> imageURL, String desc) {

  public static PostMeetingResponse from(MeetingPostContext meeting) {
    return new PostMeetingResponse(
        meeting.meetingId(),
        meeting.title(),
        meeting.category(),
        meeting.images().stream().map(PostMeetingImageResponse::from).toList(),
        meeting.description());
  }
}
