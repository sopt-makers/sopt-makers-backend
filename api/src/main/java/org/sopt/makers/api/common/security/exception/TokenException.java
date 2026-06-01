package org.sopt.makers.api.common.security.exception;

import org.sopt.makers.core.exception.BaseException;

public class TokenException extends BaseException {

  public TokenException(final TokenFailureCode failure) {
    super(failure);
  }
}
