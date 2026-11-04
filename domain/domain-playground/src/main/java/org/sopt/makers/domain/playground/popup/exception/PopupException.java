package org.sopt.makers.domain.playground.popup.exception;

import org.sopt.makers.core.exception.BaseException;

public class PopupException extends BaseException {

  public PopupException(PopupFailure failure) {
    super(failure);
  }
}
