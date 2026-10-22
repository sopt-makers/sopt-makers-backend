package org.sopt.makers.api.common.exception;

import org.sopt.makers.core.exception.BaseException;

public class CommonException extends BaseException {

  public CommonException(CommonFailureCode failure) {
    super(failure);
  }
}
