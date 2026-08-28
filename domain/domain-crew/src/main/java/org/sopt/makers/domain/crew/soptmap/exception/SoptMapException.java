package org.sopt.makers.domain.crew.soptmap.exception;

import org.sopt.makers.core.exception.BaseException;

public class SoptMapException extends BaseException {

  public SoptMapException(SoptMapFailure failure) {
    super(failure);
  }
}
