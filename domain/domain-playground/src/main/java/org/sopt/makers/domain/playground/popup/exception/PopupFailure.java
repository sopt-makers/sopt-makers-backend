package org.sopt.makers.domain.playground.popup.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PopupFailure implements FailureCode {
  NOT_FOUND_POPUP(404, "존재하지 않는 팝업입니다.");

  private final int statusCode;
  private final String message;
}
