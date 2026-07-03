package org.sopt.makers.domain.admin.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmCategory {
  NOTICE("공지"),
  NEWS("소식");

  private final String name;
}
