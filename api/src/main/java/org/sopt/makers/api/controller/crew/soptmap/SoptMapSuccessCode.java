package org.sopt.makers.api.controller.crew.soptmap;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptMapSuccessCode implements SuccessCode {
  CREATE_SOPT_MAP(201, "솝맵 장소 등록에 성공했습니다."),
  UPDATE_SOPT_MAP(200, "솝맵 장소 수정에 성공했습니다."),
  DELETE_SOPT_MAP(200, "솝맵 장소 삭제에 성공했습니다."),
  SEARCH_SUBWAY_STATION(200, "지하철역 검색에 성공했습니다."),
  GET_SOPT_MAPS(200, "솝맵 목록 조회에 성공했습니다."),
  GET_SOPT_MAP(200, "솝맵 상세 조회에 성공했습니다."),
  TOGGLE_SOPT_MAP_RECOMMEND(200, "솝맵 추천 상태 변경에 성공했습니다."),
  CHECK_SOPT_MAP_EVENT(200, "솝맵 이벤트 당첨 여부 조회에 성공했습니다."),
  GET_SOPT_MAP_GIFT(200, "솝맵 이벤트 선물 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
