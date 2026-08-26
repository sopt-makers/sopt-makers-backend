package org.sopt.makers.domain.app.push;

import java.util.Arrays;
import org.sopt.makers.domain.app.push.exception.PushException;
import org.sopt.makers.domain.app.push.exception.PushFailure;

public enum PushTokenPlatform {
  ANDROID,
  IOS;

  public static PushTokenPlatform from(String value) {
    if (value == null || value.isBlank()) {
      throw new PushException(PushFailure.INVALID_PUSH_TOKEN_PLATFORM);
    }
    return Arrays.stream(values())
        .filter(platform -> platform.name().equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new PushException(PushFailure.INVALID_PUSH_TOKEN_PLATFORM));
  }
}
