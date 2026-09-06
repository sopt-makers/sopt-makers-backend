package org.sopt.makers.domain.playground.post.exception;

import org.sopt.makers.core.exception.BaseException;

public class PostException extends BaseException {

  public PostException(PostFailure failure) {
    super(failure);
  }
}
