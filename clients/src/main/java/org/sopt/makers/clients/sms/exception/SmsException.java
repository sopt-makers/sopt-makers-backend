package org.sopt.makers.clients.sms.exception;

import org.sopt.makers.core.exception.BaseException;

public class SmsException extends BaseException {

  public SmsException(final SmsFailure failure) {
    super(failure);
  }
}
