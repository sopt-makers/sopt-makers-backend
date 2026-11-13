package org.sopt.makers.domain.crew.dining.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor
public enum DiningFailure implements FailureCode {
  DINING_IMAGE_NOT_CONFIGURED(500, "회식 기본 이미지가 설정되지 않았습니다."),
  ALREADY_ATTENDED_DINING_ROUND(400, "이미 참석 중인 회차입니다."),
  ALREADY_CLOSED_DINING_ROUND(400, "이미 마감된 회차입니다."),
  ALREADY_REQUESTED_DINING_PAYER(400, "이미 결제자 요청을 했습니다."),
  CANNOT_DELETE_CLOSED_DINING_ROUND(400, "마감된 회차는 삭제할 수 없습니다."),
  CANNOT_DELETE_DINING_WITH_CLOSED_ROUND(400, "마감된 회차가 있는 회식은 삭제할 수 없습니다."),
  CANNOT_DELETE_FIRST_DINING_ROUND(400, "1차는 회식 삭제로만 삭제할 수 있습니다."),
  CANNOT_REQUEST_DINING_PAYER_AS_LEADER(400, "개설자는 공동 결제자를 요청할 수 없습니다."),
  CSV_ERROR(500, "csv 처리 과정에 에러가 발생했습니다."),
  ENDED_DINING(400, "종료된 회식입니다."),
  FORBIDDEN_EXCEPTION(403, "권한이 없습니다."),
  INVALID_DINING_MAP_LINK(400, "네이버지도 또는 카카오맵 링크만 등록할 수 있습니다."),
  NOT_APPROVED_DINING_PAYER(400, "승인된 공동 결제자가 아닙니다."),
  NOT_FOUND_DINING(404, "존재하지 않는 회식입니다."),
  NOT_FOUND_DINING_ATTENDANCE(404, "존재하지 않는 회차 참석 기록입니다."),
  NOT_FOUND_DINING_ROUND(404, "존재하지 않는 회차입니다."),
  NOT_FOUND_USER(404, "존재하지 않는 유저입니다."),
  NOT_PENDING_DINING_PAYER(400, "승인 대기 중인 결제자 요청이 없습니다."),
  PAST_DINING_DATE(400, "지난 날짜로 회식을 등록할 수 없습니다."),
  VALIDATION_EXCEPTION(400, "CF-001");
  private final int statusCode;
  private final String message;
}
