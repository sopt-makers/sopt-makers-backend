package org.sopt.makers.domain.app.home.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum HomeFailure implements FailureCode {
  NOT_FOUND_APP_SERVICE(404, "앱 서비스 정보를 찾을 수 없습니다."),
  NOT_FOUND_USER_GENERATION(404, "기수 정보를 찾을 수 없습니다.");

  private final int statusCode;
  private final String message;
}
