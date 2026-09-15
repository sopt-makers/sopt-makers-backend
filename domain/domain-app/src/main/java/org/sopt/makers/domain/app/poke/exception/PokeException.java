package org.sopt.makers.domain.app.poke.exception;

import org.sopt.makers.core.exception.BaseException;

public class PokeException extends BaseException {

  public PokeException(PokeFailure failure) {
    super(failure);
  }
}
