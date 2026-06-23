package org.sopt.makers.clients.s3.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum S3Failure implements FailureCode {
  INVALID_CONTENT_TYPE(400, "허용되지 않는 파일 형식입니다"),
  INVALID_FILE_URL(400, "올바르지 않은 S3 파일 URL입니다"),
  FILE_UPLOAD_FAILED(500, "S3 파일 업로드에 실패했습니다"),
  FILE_DELETE_FAILED(500, "S3 파일 삭제에 실패했습니다");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
