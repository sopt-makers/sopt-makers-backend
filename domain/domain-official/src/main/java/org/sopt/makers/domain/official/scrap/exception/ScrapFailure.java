package org.sopt.makers.domain.official.scrap.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ScrapFailure implements FailureCode {
  SCRAPING_FAILED(500, "블로그 정보를 가져오는데 실패했습니다.");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
