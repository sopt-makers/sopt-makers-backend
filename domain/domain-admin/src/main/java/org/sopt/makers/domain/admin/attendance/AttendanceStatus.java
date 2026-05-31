package org.sopt.makers.domain.admin.attendance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {
  ATTENDANCE("출석"),
  ABSENT("결석"),
  TARDY("지각"),
  PARTICIPATE("ETC 참여"),
  NOT_PARTICIPATE("ETC 미참여");

  private final String name;
}
