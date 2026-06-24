package org.sopt.makers.domain.official.notification.exception;

import org.sopt.makers.core.exception.BaseException;

public class NotificationException extends BaseException {

  public NotificationException(NotificationFailure failure) {
    super(failure);
  }
}
