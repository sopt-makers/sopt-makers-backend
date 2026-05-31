package org.sopt.makers.domain.auth.exception;

import org.sopt.makers.core.exception.BaseException;

public class AuthException extends BaseException {

  public AuthException(AuthFailure failure) {
    super(failure);
  }
}
