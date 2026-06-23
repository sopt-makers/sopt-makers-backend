package org.sopt.makers.clients.s3.exception;

import org.sopt.makers.core.exception.BaseException;

public class S3Exception extends BaseException {

  public S3Exception(S3Failure failure) {
    super(failure);
  }
}
