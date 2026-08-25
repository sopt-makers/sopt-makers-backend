package org.sopt.makers.domain.app.fortune.exception;

import org.sopt.makers.core.exception.BaseException;

public class FortuneException extends BaseException {

  public FortuneException(FortuneFailure failure) {
    super(failure);
  }
}
