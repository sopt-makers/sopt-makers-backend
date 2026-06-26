package org.sopt.makers.domain.official.notification.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum NotificationFailure implements FailureCode {
  DUPLICATE_NOTIFICATION(409, "이미 등록된 알림입니다.");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
