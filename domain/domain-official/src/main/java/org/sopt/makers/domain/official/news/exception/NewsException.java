package org.sopt.makers.domain.official.news.exception;

import org.sopt.makers.core.exception.BaseException;

public class NewsException extends BaseException {

  public NewsException(NewsFailure failure) {
    super(failure);
  }
}
