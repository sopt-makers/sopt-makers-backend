package org.sopt.makers.api.controller.official.visitor;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum VisitorSuccessCode implements SuccessCode {
  VISITOR_COUNT_UP(200, "방문자 수 증가에 성공했습니다."),
  GET_TODAY_VISITOR(200, "오늘 방문자 수 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
