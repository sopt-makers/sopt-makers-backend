package org.sopt.makers.domain.admin.app.exception;

import org.sopt.makers.core.exception.BaseException;

public class AppException extends BaseException {

  public AppException(AppFailure failure) {
    super(failure);
  }
}
