package org.sopt.makers.domain.crew.flash;

import static org.sopt.makers.domain.crew.flash.exception.FlashFailure.INVALID_FLASH_PLACE_TYPE;

import java.util.Arrays;
import org.sopt.makers.domain.crew.flash.exception.FlashException;

public enum FlashPlaceType {
  OFFLINE("오프라인"),
  ONLINE("온라인"),
  AFTER_DISCUSSION("협의 후 결정");

  private final String value;

  FlashPlaceType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static FlashPlaceType ofValue(String value) {
    if (value == null || value.isBlank()) {
      throw new FlashException(INVALID_FLASH_PLACE_TYPE);
    }
    String normalized = value.trim();
    return Arrays.stream(values())
        .filter(type -> type.name().equalsIgnoreCase(normalized) || type.value.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new FlashException(INVALID_FLASH_PLACE_TYPE));
  }
}
