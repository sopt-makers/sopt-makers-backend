package org.sopt.makers.domain.admin.lecture.exception;

import org.sopt.makers.core.exception.BaseException;

public class LectureException extends BaseException {

  public LectureException(LectureFailure failure) {
    super(failure);
  }
}
