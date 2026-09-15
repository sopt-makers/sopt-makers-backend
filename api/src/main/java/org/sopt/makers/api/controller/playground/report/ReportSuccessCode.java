package org.sopt.makers.api.controller.playground.report;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ReportSuccessCode implements SuccessCode {
  GET_REPORT_STATS(200, "솝트 리포트 통계 조회가 완료되었습니다"),
  GET_MY_REPORT_STATS(200, "나의 솝트 리포트 통계 조회가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
