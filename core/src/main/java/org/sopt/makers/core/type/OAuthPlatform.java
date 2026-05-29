package org.sopt.makers.core.type;

import java.util.Arrays;

public enum OAuthPlatform {
  GOOGLE,
  APPLE,
  FACEBOOK;

  public static OAuthPlatform find(final String platform) {
    return Arrays.stream(OAuthPlatform.values())
        .filter(p -> p.name().equals(platform))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 OAuth 플랫폼입니다: " + platform));
  }
}
