package org.sopt.makers.core.type;

import java.util.Arrays;

public enum OAuthPlatform {
  GOOGLE,
  APPLE,
  FACEBOOK;

  public static OAuthPlatform find(final String platform) {
    if (platform == null || platform.isEmpty()) {
      throw new IllegalArgumentException("OAuth 플랫폼은 필수 값입니다.");
    }

    return Arrays.stream(OAuthPlatform.values())
        .filter(p -> p.name().equalsIgnoreCase(platform))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 OAuth 플랫폼입니다: " + platform));
  }
}
