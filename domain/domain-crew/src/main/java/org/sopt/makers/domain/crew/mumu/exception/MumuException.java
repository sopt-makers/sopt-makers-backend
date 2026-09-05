package org.sopt.makers.domain.crew.mumu.exception;

import org.sopt.makers.core.exception.BaseException;

public class MumuException extends BaseException {

  public MumuException(MumuFailure failure) {
    super(failure);
  }
}
