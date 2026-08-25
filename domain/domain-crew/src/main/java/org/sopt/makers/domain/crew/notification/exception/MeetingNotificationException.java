package org.sopt.makers.domain.crew.notification.exception;

import org.sopt.makers.core.exception.BaseException;

public class MeetingNotificationException extends BaseException {

  public MeetingNotificationException(MeetingNotificationFailure failure) {
    super(failure);
  }
}
