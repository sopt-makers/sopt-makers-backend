package org.sopt.makers.domain.admin.banner.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum BannerFailure implements FailureCode {
  NOT_FOUND_BANNER(404, "존재하지 않는 배너입니다"),
  NOT_FOUND_LOCATION(400, "지원하지 않는 배너 게시 위치입니다"),
  NOT_FOUND_CONTENT_TYPE(400, "지원하지 않는 배너 콘텐츠 타입입니다"),
  NOT_FOUND_STATUS(400, "지원하지 않는 배너 상태입니다"),
  INVALID_BANNER_PERIOD(400, "배너 게시 기간이 올바르지 않습니다"),
  INVALID_BANNER_DATE_FORMAT(400, "배너 날짜 형식이 올바르지 않습니다"),
  INVALID_BANNER_PROGRESS_STATUS_PARAMETER(400, "지원하지 않는 배너 상태 파라미터입니다"),
  INVALID_BANNER_SORT_CRITERIA_PARAMETER(400, "지원하지 않는 배너 정렬 파라미터입니다"),
  CANNOT_DELETE_DONE_BANNER(400, "게시 종료된 배너는 삭제할 수 없습니다"),
  CANNOT_MODIFY_DONE_BANNER(400, "게시 종료된 배너는 수정할 수 없습니다"),
  FILE_UPLOAD_FAILED(500, "배너 이미지 업로드에 실패했습니다");

  private final int statusCode;
  private final String message;
}
