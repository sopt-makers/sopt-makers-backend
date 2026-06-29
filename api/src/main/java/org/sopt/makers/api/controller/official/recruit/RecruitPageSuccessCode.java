package org.sopt.makers.api.controller.official.recruit;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum RecruitPageSuccessCode implements SuccessCode {
  GET_RECRUIT_MAIN_PAGE(200, "지원서 메인 페이지 조회에 성공했습니다."),
  GET_RECRUIT_PART_DETAIL(200, "지원서 파트 상세 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
