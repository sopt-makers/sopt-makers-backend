package org.sopt.makers.domain.official.review.exception;

import org.sopt.makers.core.exception.BaseException;

public class ReviewException extends BaseException {

  public ReviewException(ReviewFailure failure) {
    super(failure);
  }
}
