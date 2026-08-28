package org.sopt.makers.domain.crew.advertisement.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdvertisementFailure implements FailureCode {
  NOT_FOUND_ADVERTISEMENT(404, "광고를 찾을 수 없습니다."),
  NOT_FOUND_ADVERTISEMENT_USER(404, "광고 대상 사용자를 찾을 수 없습니다."),
  INVALID_ADVERTISEMENT_CATEGORY(400, "일반 광고 조회에서 허용하지 않는 카테고리입니다."),
  INVALID_MEETING_TOP_ADVERTISEMENT(400, "모임 상단 광고만 수정할 수 있습니다."),
  INVALID_ADVERTISEMENT_UPDATE(400, "수정할 모임 상단 광고 필드가 없습니다."),
  INVALID_ADVERTISEMENT_PERIOD(400, "광고 시작일은 종료일보다 이후일 수 없습니다."),
  INVALID_ADVERTISEMENT_VALUE(400, "광고 입력 값이 올바르지 않습니다."),
  DUPLICATE_DISPLAYED_MEETING_TOP(400, "모임 상단 광고는 하나만 노출할 수 있습니다."),
  INVALID_ADVERTISEMENT_IMAGE(400, "업로드할 광고 이미지가 올바르지 않습니다.");

  private final int statusCode;
  private final String message;
}
