package org.sopt.makers.domain.playground.post;

import java.util.List;

public record MeetingPostContext(
    Long meetingId,
    String title,
    String category,
    List<PostMeetingImage> images,
    String description,
    boolean member) {

  public MeetingPostContext {
    images = images == null ? List.of() : List.copyOf(images);
  }
}
