package org.sopt.makers.api.controller.playground.member;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MemberSuccessCode implements SuccessCode {
  GET_MEMBER(200, "유저 조회 성공"),
  GET_MY_INFO(200, "내 정보 조회 성공"),
  SEARCH_MEMBER(200, "멤버 검색 성공"),
  CREATE_PROFILE(201, "유저 프로필 생성 성공"),
  UPDATE_PROFILE(200, "멤버 프로필 수정 성공"),
  UPDATE_WORK_PREFERENCE(200, "작업 성향이 성공적으로 업데이트되었습니다."),
  GET_WORK_PREFERENCE(200, "작업 성향 조회 성공"),
  GET_PROFILE(200, "멤버 프로필 조회 성공"),
  GET_PROFILE_LIST(200, "멤버 프로필 목록 조회 성공"),
  CHECK_ACTIVITY(200, "활동 기수 확인 여부 변경 성공"),
  DELETE_PROFILE_LINK(200, "성공적으로 link를 삭제했습니다.");

  private final int statusCode;
  private final String message;
}
