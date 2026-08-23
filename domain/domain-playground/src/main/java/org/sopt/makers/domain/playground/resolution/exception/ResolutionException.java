package org.sopt.makers.domain.playground.resolution.exception;

import org.sopt.makers.core.exception.BaseException;

public class ResolutionException extends BaseException {

  public ResolutionException(ResolutionFailure failure) {
    super(failure);
  }
}
