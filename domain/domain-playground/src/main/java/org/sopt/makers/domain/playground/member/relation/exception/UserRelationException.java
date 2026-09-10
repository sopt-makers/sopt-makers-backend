package org.sopt.makers.domain.playground.member.relation.exception;

import org.sopt.makers.core.exception.BaseException;

public class UserRelationException extends BaseException {

  public UserRelationException(UserRelationFailure failure) {
    super(failure);
  }
}
