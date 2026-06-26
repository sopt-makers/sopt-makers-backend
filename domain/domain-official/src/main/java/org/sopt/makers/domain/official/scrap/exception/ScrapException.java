package org.sopt.makers.domain.official.scrap.exception;

import org.sopt.makers.core.exception.BaseException;

public class ScrapException extends BaseException {
  public ScrapException(ScrapFailure failure) {
    super(failure);
  }
}
