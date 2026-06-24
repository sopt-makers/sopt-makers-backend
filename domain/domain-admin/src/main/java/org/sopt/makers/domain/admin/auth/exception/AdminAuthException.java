package org.sopt.makers.domain.admin.auth.exception;

import org.sopt.makers.core.exception.BaseException;

public class AdminAuthException extends BaseException {

  public AdminAuthException(AdminAuthFailure failure) {
    super(failure);
  }
}
