package org.sopt.makers.domain.playground.member.profile.exception;

import org.sopt.makers.core.exception.BaseException;

public class UserProfileException extends BaseException {

  public UserProfileException(UserProfileFailure failure) {
    super(failure);
  }
}
