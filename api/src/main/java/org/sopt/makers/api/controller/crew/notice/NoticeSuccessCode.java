package org.sopt.makers.api.controller.crew.notice;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum NoticeSuccessCode implements SuccessCode {
  GET_NOTICES(200, "공지 목록 조회에 성공했습니다."),
  CREATE_NOTICE(201, "공지 생성에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
