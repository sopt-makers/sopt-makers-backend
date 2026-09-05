package org.sopt.makers.api.controller.app.soptamp;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppjamRankSuccessCode implements SuccessCode {
  GET_RECENT_TEAM_RANKS(200, "앱잼팀 랭킹 최근 인증 미션 조회에 성공했습니다."),
  GET_TODAY_TEAM_RANKS(200, "앱잼팀 오늘의 득점 랭킹 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
