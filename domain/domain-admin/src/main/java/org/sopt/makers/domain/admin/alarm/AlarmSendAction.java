package org.sopt.makers.domain.admin.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmSendAction {
  SEND("send"),
  SEND_ALL("sendAll");

  private final String value;
}
