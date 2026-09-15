package org.sopt.makers.domain.app.soptamp.clap;

public record Clap(Long id, Long stampId, Long userId, int clapCount, Long version) {

  public static final int MAX_CLAP_COUNT = 50;

  public static Clap create(Long stampId, Long userId) {
    return new Clap(null, stampId, userId, 0, null);
  }

  public int applicableIncrement(int increment) {
    if (increment <= 0) {
      return 0;
    }
    return Math.min(increment, MAX_CLAP_COUNT - clapCount);
  }
}
