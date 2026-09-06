package org.sopt.makers.api.controller.admin.crew.mumu;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminMumuTextSuccessCode implements SuccessCode {
  GET_MUMU_TEXTS(200, "무무 텍스트 목록 조회에 성공했습니다."),
  CREATE_MUMU_TEXT(201, "무무 텍스트 생성에 성공했습니다."),
  UPDATE_MUMU_TEXT(200, "무무 텍스트 수정에 성공했습니다."),
  DELETE_MUMU_TEXT(200, "무무 텍스트 삭제에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
