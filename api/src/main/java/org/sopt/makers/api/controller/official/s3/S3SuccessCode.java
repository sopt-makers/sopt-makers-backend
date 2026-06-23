package org.sopt.makers.api.controller.official.s3;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum S3SuccessCode implements SuccessCode {
  CREATE_PRESIGNED_URL(200, "Presigned URL 발급에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
