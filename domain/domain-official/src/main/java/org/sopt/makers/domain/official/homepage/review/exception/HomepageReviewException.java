package org.sopt.makers.domain.official.homepage.review.exception;

import org.sopt.makers.core.exception.BaseException;

public class HomepageReviewException extends BaseException {

  public HomepageReviewException(HomepageReviewFailure failure) {
    super(failure);
  }
}
