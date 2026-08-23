package org.sopt.makers.domain.playground.resolution.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ResolutionFailure implements FailureCode {
  NOT_FOUND_USER(404, "존재하지 않는 유저입니다"),
  NOT_FOUND_RESOLUTION(404, "다짐 메시지가 존재하지 않습니다"),
  INVALID_RESOLUTION_TAG(400, "유효하지 않은 다짐 태그입니다"),
  NO_ACTIVITIES(400, "솝트 활동 이력이 없습니다"),
  NOT_CURRENT_GENERATION(400, "현재 기수 멤버만 다짐 메시지를 등록할 수 있습니다"),
  ALREADY_EXISTS_RESOLUTION(400, "이미 다짐 메시지가 존재합니다");

  private final int statusCode;
  private final String message;
}
