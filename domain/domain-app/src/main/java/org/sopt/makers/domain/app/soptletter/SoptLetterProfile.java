package org.sopt.makers.domain.app.soptletter;

public record SoptLetterProfile(Long id, Long userId, String nickname, boolean isOnboarded) {

  public static SoptLetterProfile create(Long userId, String nickname) {
    return new SoptLetterProfile(null, userId, nickname, false);
  }
}
