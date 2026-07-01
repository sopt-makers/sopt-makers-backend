package org.sopt.makers.domain.official.admin.exception;

import org.sopt.makers.core.exception.BaseException;

public class OfficialAdminException extends BaseException {

  public OfficialAdminException(OfficialAdminFailure failure) {
    super(failure);
  }
}
