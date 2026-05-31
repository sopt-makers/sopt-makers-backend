package org.sopt.makers.domain.admin.attendance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LectureAttribute {
  SEMINAR("세미나"),
  EVENT("행사"),
  ETC("기타");

  private final String name;
}
