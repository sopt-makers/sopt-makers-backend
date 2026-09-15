package org.sopt.makers.domain.playground.member.ask.exception;

import org.sopt.makers.core.exception.BaseException;

public class UserAskException extends BaseException {

  public UserAskException(UserAskFailure failure) {
    super(failure);
  }
}
