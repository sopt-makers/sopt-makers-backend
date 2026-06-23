package org.sopt.makers.domain.official.homepage.news.exception;

import org.sopt.makers.core.exception.BaseException;

public class NewsException extends BaseException {

  public NewsException(NewsFailure failure) {
    super(failure);
  }
}
