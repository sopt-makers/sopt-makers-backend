package org.sopt.makers.domain.crew.flash;

import static org.sopt.makers.domain.crew.flash.exception.FlashFailure.INVALID_FLASH_TIMING_TYPE;

import java.util.Arrays;
import org.sopt.makers.domain.crew.flash.exception.FlashException;

public enum FlashTimingType {
  IMMEDIATE("당일"),
  AFTER_DISCUSSION("예정 기간 (협의 후 결정)");

  private final String value;

  FlashTimingType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static FlashTimingType ofValue(String value) {
    if (value == null || value.isBlank()) {
      throw new FlashException(INVALID_FLASH_TIMING_TYPE);
    }
    String normalized = value.trim();
    return Arrays.stream(values())
        .filter(type -> type.name().equalsIgnoreCase(normalized) || type.value.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new FlashException(INVALID_FLASH_TIMING_TYPE));
  }
}
