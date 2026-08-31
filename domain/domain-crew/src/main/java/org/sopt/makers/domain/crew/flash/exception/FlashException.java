package org.sopt.makers.domain.crew.flash.exception;

import org.sopt.makers.core.exception.BaseException;

public class FlashException extends BaseException {

  public FlashException(FlashFailure failure) {
    super(failure);
  }
}
