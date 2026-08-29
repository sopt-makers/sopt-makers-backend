package org.sopt.makers.domain.crew.property.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CrewPropertyFailure implements FailureCode {
  NOT_FOUND_CREW_PROPERTY(404, "CREW 프로퍼티를 찾을 수 없습니다."),
  INVALID_HOME_PROPERTY(500, "홈 프로퍼티 형식이 올바르지 않습니다.");

  private final int statusCode;
  private final String message;
}
