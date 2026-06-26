package org.sopt.makers.api.controller.internal;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum InternalScrapSuccessCode implements SuccessCode {
  SCRAP_LINK(200, "블로그 링크 스크랩에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
