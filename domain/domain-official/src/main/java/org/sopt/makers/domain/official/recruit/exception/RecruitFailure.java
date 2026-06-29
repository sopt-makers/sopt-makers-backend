package org.sopt.makers.domain.official.recruit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor
public enum RecruitFailure implements FailureCode {
  GENERATION_NOT_FOUND(404, "기수 정보가 존재하지 않습니다"),
  PART_NOT_FOUND(404, "파트 정보가 존재하지 않습니다"),
  RECRUIT_PART_INTRODUCTION_NOT_FOUND(404, "파트 소개 정보가 존재하지 않습니다"),
  INVALID_PART(400, "유효하지 않은 파트입니다");

  private final int statusCode;
  private final String message;
}
