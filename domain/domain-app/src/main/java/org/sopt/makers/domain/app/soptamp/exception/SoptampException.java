package org.sopt.makers.domain.app.soptamp.exception;

import org.sopt.makers.core.exception.BaseException;

public class SoptampException extends BaseException {

  public SoptampException(SoptampFailure failure) {
    super(failure);
  }
}
