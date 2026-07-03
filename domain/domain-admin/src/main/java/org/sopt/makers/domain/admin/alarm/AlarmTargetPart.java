package org.sopt.makers.domain.admin.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;

@Getter
@RequiredArgsConstructor
public enum AlarmTargetPart {
  ALL("전체"),
  PLAN("기획"),
  DESIGN("디자인"),
  WEB("웹"),
  ANDROID("안드로이드"),
  iOS("iOS"),
  SERVER("서버"),
  UNDEFINED("없음");

  private final String name;

  public Part toPart() {
    return switch (this) {
      case ALL -> Part.ALL;
      case PLAN -> Part.PLAN;
      case DESIGN -> Part.DESIGN;
      case ANDROID -> Part.ANDROID;
      case iOS -> Part.IOS;
      case WEB -> Part.WEB;
      case SERVER -> Part.SERVER;
      default -> throw new IllegalArgumentException("파트로 변환할 수 없는 AlarmTargetPart: " + this);
    };
  }
}
