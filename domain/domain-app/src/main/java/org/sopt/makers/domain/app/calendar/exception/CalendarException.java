package org.sopt.makers.domain.app.calendar.exception;

import org.sopt.makers.core.exception.BaseException;

public class CalendarException extends BaseException {

  public CalendarException(CalendarFailure failure) {
    super(failure);
  }
}
