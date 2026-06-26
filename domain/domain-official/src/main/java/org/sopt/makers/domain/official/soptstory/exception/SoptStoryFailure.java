package org.sopt.makers.domain.official.soptstory.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptStoryFailure implements FailureCode {
  DUPLICATE_SOPT_STORY_URL(400, "이미 등록된 솝트스토리 URL입니다"),
  ALREADY_LIKED(400, "이미 좋아요를 누른 솝트스토리입니다"),
  NOT_LIKED(400, "좋아요를 누르지 않은 솝트스토리입니다"),
  INVALID_LIKE_COUNT(400, "좋아요 개수는 음수가 될 수 없습니다"),
  INVALID_CLIENT_IP(400, "클라이언트 IP를 확인할 수 없습니다"),
  SCRAP_FAILED(500, "아티클 스크래핑에 실패했습니다"),
  NOT_FOUND_SOPT_STORY(404, "존재하지 않는 솝트스토리입니다");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
