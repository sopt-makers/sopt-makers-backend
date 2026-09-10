package org.sopt.makers.domain.playground.community.exception;

import org.sopt.makers.core.exception.BaseException;

public class CommunityException extends BaseException {

  public CommunityException(CommunityFailure failure) {
    super(failure);
  }
}
