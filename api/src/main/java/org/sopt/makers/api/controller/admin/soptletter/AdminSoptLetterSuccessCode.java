package org.sopt.makers.api.controller.admin.soptletter;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminSoptLetterSuccessCode implements SuccessCode {
  SUCCESS_SEND_SOPT_LETTER_UNWRITTEN_REMINDER(200, "솝레터 미작성 리마인드 알림 발송이 완료되었습니다"),
  SUCCESS_SEND_SOPT_LETTER_NEW_TOPIC(200, "솝레터 새 주제 알림 발송이 완료되었습니다");

  private final int statusCode;
  private final String message;
}
