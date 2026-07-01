package org.sopt.makers.api.controller.admin.official;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminOfficialSuccessCode implements SuccessCode {
  SUCCESS_GET_ADMIN_MAIN(200, "공홈 전체 조회가 완료되었습니다"),
  SUCCESS_ADD_COMMON(201, "공통 탭 데이터 전달이 완료되었습니다"),
  SUCCESS_CONFIRM_COMMON(201, "공통 탭 배포가 완료되었습니다"),
  SUCCESS_ADD_HOME(201, "홈 탭 데이터 전달이 완료되었습니다"),
  SUCCESS_CONFIRM_HOME(201, "홈 탭 배포가 완료되었습니다"),
  SUCCESS_ADD_ABOUT(201, "소개 탭 데이터 전달이 완료되었습니다"),
  SUCCESS_CONFIRM_ABOUT(201, "소개 탭 배포가 완료되었습니다"),
  SUCCESS_ADD_RECRUIT(201, "모집안내 탭 데이터 전달이 완료되었습니다"),
  SUCCESS_CONFIRM_RECRUIT(201, "모집안내 탭 배포가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
