package org.sopt.makers.domain.app.notification.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum NotificationFailure implements FailureCode {
  NOT_FOUND_NOTIFICATION(404, "존재하지 않는 알림입니다.");

  private final int statusCode;
  private final String message;
}
