package org.sopt.makers.domain.crew.meeting.exception;

import org.sopt.makers.core.exception.BaseException;

public class MeetingException extends BaseException {

  public MeetingException(MeetingFailure failure) {
    super(failure);
  }
}
