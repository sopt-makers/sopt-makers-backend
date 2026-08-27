package org.sopt.makers.domain.crew.notice.exception;

import org.sopt.makers.core.exception.BaseException;

public class NoticeException extends BaseException {

  public NoticeException(NoticeFailure failure) {
    super(failure);
  }
}
