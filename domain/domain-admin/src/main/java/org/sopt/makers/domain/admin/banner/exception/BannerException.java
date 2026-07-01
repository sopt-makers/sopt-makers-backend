package org.sopt.makers.domain.admin.banner.exception;

import org.sopt.makers.core.exception.BaseException;

public class BannerException extends BaseException {

  public BannerException(BannerFailure failure) {
    super(failure);
  }
}
