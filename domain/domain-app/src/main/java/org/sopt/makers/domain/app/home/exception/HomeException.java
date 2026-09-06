package org.sopt.makers.domain.app.home.exception;

import org.sopt.makers.core.exception.BaseException;

public class HomeException extends BaseException {

  public HomeException(HomeFailure failure) {
    super(failure);
  }
}
