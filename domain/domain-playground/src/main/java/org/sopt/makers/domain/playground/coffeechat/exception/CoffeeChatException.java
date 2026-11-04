package org.sopt.makers.domain.playground.coffeechat.exception;

import org.sopt.makers.core.exception.BaseException;

public class CoffeeChatException extends BaseException {

  public CoffeeChatException(CoffeeChatFailure failure) {
    super(failure);
  }
}
