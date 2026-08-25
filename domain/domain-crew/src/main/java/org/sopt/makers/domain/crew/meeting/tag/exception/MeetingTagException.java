package org.sopt.makers.domain.crew.meeting.tag.exception;

import org.sopt.makers.core.exception.BaseException;

public class MeetingTagException extends BaseException {

  public MeetingTagException(MeetingTagFailure failure) {
    super(failure);
  }
}
