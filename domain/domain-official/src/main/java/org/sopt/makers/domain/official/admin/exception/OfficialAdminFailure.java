package org.sopt.makers.domain.official.admin.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum OfficialAdminFailure implements FailureCode {
  COMMON_CACHE_NOT_FOUND(400, "공통 탭 배포 1단계를 먼저 호출하세요"),
  HOME_CACHE_NOT_FOUND(400, "홈 탭 배포 1단계를 먼저 호출하세요"),
  ABOUT_CACHE_NOT_FOUND(400, "소개 탭 배포 1단계를 먼저 호출하세요"),
  RECRUIT_CACHE_NOT_FOUND(400, "모집안내 탭 배포 1단계를 먼저 호출하세요");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
