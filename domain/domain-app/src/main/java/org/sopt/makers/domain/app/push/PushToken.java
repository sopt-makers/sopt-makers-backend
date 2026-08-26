package org.sopt.makers.domain.app.push;

public record PushToken(Long id, Long userId, String token, PushTokenPlatform platform) {

  public static PushToken create(Long userId, String token, PushTokenPlatform platform) {
    return new PushToken(null, userId, token, platform);
  }
}
