package org.sopt.makers.domain.crew.notification.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MeetingNotificationFailure implements FailureCode {
  FAIL_SEND_MEETING_NOTIFICATION(502, "모임 알림 발송에 실패했습니다.");

  private final int statusCode;
  private final String message;
}
