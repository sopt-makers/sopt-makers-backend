package org.sopt.makers.domain.admin.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {
  INSTANT("즉시 발송"),
  RESERVED("예약 발송");

  private final String description;
}
