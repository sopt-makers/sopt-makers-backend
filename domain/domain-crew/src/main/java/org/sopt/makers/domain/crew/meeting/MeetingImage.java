package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEETING_VALUE;

import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public record MeetingImage(Integer id, String url) {

  public MeetingImage {
    if (id == null || id < 0 || url == null || url.isBlank()) {
      throw new MeetingException(INVALID_MEETING_VALUE);
    }
  }
}
