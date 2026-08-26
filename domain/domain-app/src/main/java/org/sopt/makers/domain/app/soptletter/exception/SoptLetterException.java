package org.sopt.makers.domain.app.soptletter.exception;

import org.sopt.makers.core.exception.BaseException;

public class SoptLetterException extends BaseException {

  public SoptLetterException(SoptLetterFailure failure) {
    super(failure);
  }
}
