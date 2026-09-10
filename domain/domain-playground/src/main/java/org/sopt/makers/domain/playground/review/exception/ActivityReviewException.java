package org.sopt.makers.domain.playground.review.exception;

import org.sopt.makers.core.exception.BaseException;

public class ActivityReviewException extends BaseException {

  public ActivityReviewException(ActivityReviewFailure failure) {
    super(failure);
  }
}
