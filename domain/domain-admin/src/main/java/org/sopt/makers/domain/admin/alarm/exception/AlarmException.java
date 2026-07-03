package org.sopt.makers.domain.admin.alarm.exception;

import org.sopt.makers.core.exception.BaseException;

public class AlarmException extends BaseException {

  public AlarmException(AlarmFailure failure) {
    super(failure);
  }
}
