package org.sopt.makers.api.controller.app.soptamp;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptampStorageSuccessCode implements SuccessCode {
  GET_STAMP_PRESIGNED_URL(200, "스탬프 pre-signed url 조회에 성공했습니다."),
  GET_MISSION_PRESIGNED_URL(200, "미션 pre-signed url 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
