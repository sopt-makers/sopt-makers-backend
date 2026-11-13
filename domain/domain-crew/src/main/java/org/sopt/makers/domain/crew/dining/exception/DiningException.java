package org.sopt.makers.domain.crew.dining.exception;

import org.sopt.makers.core.exception.BaseException;

public class DiningException extends BaseException {
  public DiningException(DiningFailure failure) {
    super(failure);
  }
}
