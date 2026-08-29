package org.sopt.makers.api.controller.crew.property;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CrewPropertySuccessCode implements SuccessCode {
  GET_CREW_PROPERTY(200, "CREW 프로퍼티 조회에 성공했습니다."),
  GET_HOME_PROPERTY(200, "CREW 홈 프로퍼티 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
