package org.sopt.makers.domain.crew.meeting.demand.exception;

import org.sopt.makers.core.exception.BaseException;

public class MeetingDemandException extends BaseException {

  public MeetingDemandException(MeetingDemandFailure failure) {
    super(failure);
  }
}
