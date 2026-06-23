package org.sopt.makers.domain.user.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum UserFailure implements FailureCode {

  // 400
  MULTIPLE_CURRENT_CAREERS(400, "현재 직장이 2개 이상입니다"),
  INVALID_CAREER_DATE(400, "커리어 시작 날짜가 종료 날짜보다 늦을 수 없습니다"),
  DUPLICATE_ACTIVITY(400, "이미 존재하는 활동 정보입니다"),
  INVALID_ACTIVITY_GENERATION(400, "기수는 1 이상이어야 합니다"),
  ROLE_REQUIRES_PART(400, "해당 Role은 Part가 필수입니다"),
  BAD_REQUEST_INVALID_USER_SEARCH_CONDITION(400, "조회 조건 중 최소 하나는 입력해야 합니다."),
  INVALID_PROFILE_NAME(400, "이름은 필수 값입니다"),
  INVALID_PROFILE_PHONE(400, "전화번호는 필수 값입니다"),
  INVALID_SOCIAL_ACCOUNT(400, "소셜 계정 정보는 필수 값입니다"),
  INVALID_ACTIVITY(400, "활동 정보는 필수 값입니다"),

  // 404
  NOT_FOUND_PART(404, "존재하지 않는 파트입니다"),
  NOT_FOUND_ROLE(404, "존재하지 않는 역할입니다"),
  NOT_FOUND_TEAM(404, "존재하지 않는 팀입니다"),
  NOT_FOUND_USER(404, "존재하지 않는 회원입니다"),
  NOT_FOUND_USER_ACTIVITY(404, "유저가 활동한 기수가 아닙니다");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
