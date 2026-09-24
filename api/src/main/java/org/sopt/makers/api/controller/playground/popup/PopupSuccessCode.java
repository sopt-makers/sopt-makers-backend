package org.sopt.makers.api.controller.playground.popup;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PopupSuccessCode implements SuccessCode {
  CREATE_POPUP(201, "팝업 생성 성공"),
  GET_ALL_POPUPS(200, "전체 팝업 조회 성공"),
  GET_POPUP(200, "팝업 조회 성공"),
  UPDATE_POPUP(200, "팝업 수정 성공"),
  DELETE_POPUP(200, "팝업 삭제 성공"),
  GET_CURRENT_POPUP(200, "현재 팝업 조회 성공"),
  VALIDATE_ADMIN_KEY(200, "Admin Key 검증 성공");

  private final int statusCode;
  private final String message;
}
