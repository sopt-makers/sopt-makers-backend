package org.sopt.makers.domain.playground.member.profile.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum UserProfileFailure implements FailureCode {
  NOT_FOUND_MEMBER(404, "해당 id의 Member를 찾을 수 없습니다."),
  NOT_FOUND_PROFILE(400, "해당 Member는 프로필이 없습니다."),
  NOT_FOUND_LINK(404, "Member Profile Link"),
  NOT_FOUND_LEGACY_GENERATION_MEMBER(404, "30기 이전 기수 활동 회원은 공식 채널로 문의해주시기 바랍니다."),
  INVALID_ACTIVITY_TEAM(400, "잘못된 솝트 활동 팀 이름입니다."),
  ACTIVITY_GENERATION_MISMATCH(400, "요청된 활동 기수 정보가 유저의 기존 정보와 일치하지 않습니다."),
  MULTIPLE_CURRENT_CAREERS(400, "현재 직장이 2개 이상입니다."),
  NO_WORK_PREFERENCE(400, "작업 성향이 설정되지 않았습니다.");

  private final int statusCode;
  private final String message;
}
