package org.sopt.makers.domain.crew.soptmap.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptMapFailure implements FailureCode {
  NOT_FOUND_SOPT_MAP(404, "솝맵 장소를 찾을 수 없습니다."),
  NOT_FOUND_SUBWAY_STATION(404, "지하철역을 찾을 수 없습니다."),
  NOT_FOUND_EVENT_POLICY(404, "솝맵 이벤트 설정을 찾을 수 없습니다."),
  NOT_FOUND_EVENT_GIFT(404, "솝맵 이벤트 선물을 찾을 수 없습니다."),
  FORBIDDEN_SOPT_MAP(403, "솝맵 장소에 대한 권한이 없습니다."),
  INVALID_SOPT_MAP_VALUE(400, "솝맵 입력 값이 올바르지 않습니다."),
  DUPLICATE_SOPT_MAP_PLACE(400, "이미 등록된 장소입니다."),
  INVALID_SOPT_MAP_EVENT(400, "솝맵 이벤트 요청이 올바르지 않습니다.");

  private final int statusCode;
  private final String message;
}
