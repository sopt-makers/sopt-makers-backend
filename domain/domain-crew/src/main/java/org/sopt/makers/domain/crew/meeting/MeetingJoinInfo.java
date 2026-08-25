package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEETING_VALUE;

import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public record MeetingJoinInfo(MeetingType meetingType, MeetingFrequency meetingFrequency) {

  public MeetingJoinInfo {
    if (meetingType == null || meetingFrequency == null) {
      throw new MeetingException(INVALID_MEETING_VALUE);
    }
  }
}
