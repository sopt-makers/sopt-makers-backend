package org.sopt.makers.domain.crew.advertisement.exception;

import org.sopt.makers.core.exception.BaseException;

public class AdvertisementException extends BaseException {

  public AdvertisementException(AdvertisementFailure failure) {
    super(failure);
  }
}
