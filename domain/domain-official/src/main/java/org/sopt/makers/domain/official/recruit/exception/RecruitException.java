package org.sopt.makers.domain.official.recruit.exception;

import org.sopt.makers.core.exception.BaseException;

public class RecruitException extends BaseException {

  public RecruitException(RecruitFailure error) {
    super(error);
  }
}
