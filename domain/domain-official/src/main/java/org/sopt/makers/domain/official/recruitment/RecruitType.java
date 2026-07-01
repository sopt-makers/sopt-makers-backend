package org.sopt.makers.domain.official.recruitment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitType {
  OB("OB"),
  YB("YB");

  private final String code;

  public static RecruitType fromString(String type) {
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException("Recruit type must not be blank");
    }
    return switch (type.trim().toUpperCase()) {
      case "OB" -> OB;
      case "YB" -> YB;
      default -> throw new IllegalArgumentException("Unknown recruit type: " + type);
    };
  }
}
