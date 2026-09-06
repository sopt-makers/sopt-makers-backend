package org.sopt.makers.api.controller.app.soptamp;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptampSuccessCode implements SuccessCode {
  GET_CURRENT_RANKS(200, "현재 기수 랭킹 목록 조회에 성공했습니다."),
  GET_CURRENT_RANKS_BY_PART(200, "파트별 현재 기수 랭킹 목록 조회에 성공했습니다."),
  GET_PART_RANKS(200, "파트끼리의 랭킹 목록 조회에 성공했습니다."),
  GET_RANK_DETAIL(200, "유저 미션 정보 상세 조회에 성공했습니다."),
  GET_SOPTAMP_USER(200, "솝탬프 정보 조회에 성공했습니다."),
  EDIT_PROFILE_MESSAGE(200, "한마디 변경에 성공했습니다."),
  ADD_CLAP(200, "스탬프 박수치기에 성공했습니다."),
  GET_CLAPPERS(200, "박수 친 유저 목록 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
