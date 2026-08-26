package org.sopt.makers.domain.app.push.exception;

import org.sopt.makers.core.exception.BaseException;

public class PushException extends BaseException {

  public PushException(PushFailure failure) {
    super(failure);
  }
}
