package org.sopt.makers.domain.user.exception;

import org.sopt.makers.core.exception.BaseException;

public class UserException extends BaseException {

  public UserException(UserFailure failure) {
    super(failure);
  }
}
