package org.sopt.makers.domain.app.operationconfig.exception;

import org.sopt.makers.core.exception.BaseException;

public class OperationConfigException extends BaseException {

  public OperationConfigException(OperationConfigFailure failure) {
    super(failure);
  }
}
