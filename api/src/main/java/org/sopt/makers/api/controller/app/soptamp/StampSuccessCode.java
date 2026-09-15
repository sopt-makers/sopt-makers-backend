package org.sopt.makers.api.controller.app.soptamp;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum StampSuccessCode implements SuccessCode {
  GET_STAMP(200, "스탬프 조회에 성공했습니다."),
  REGISTER_STAMP(200, "스탬프 등록에 성공했습니다."),
  EDIT_STAMP(200, "스탬프 수정에 성공했습니다."),
  DELETE_STAMP(200, "스탬프 삭제에 성공했습니다."),
  DELETE_ALL_STAMPS(200, "전체 스탬프 삭제에 성공했습니다."),
  GET_REPORT_URL(200, "솝탬프 신고 URL 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
