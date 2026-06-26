package org.sopt.makers.domain.official.soptstory.exception;

import org.sopt.makers.core.exception.BaseException;

public class SoptStoryException extends BaseException {

  public SoptStoryException(SoptStoryFailure failure) {
    super(failure);
  }
}
