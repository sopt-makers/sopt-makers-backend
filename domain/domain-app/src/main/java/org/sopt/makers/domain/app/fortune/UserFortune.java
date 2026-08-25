package org.sopt.makers.domain.app.fortune;

import java.time.LocalDate;

public record UserFortune(Long id, Long userId, Long fortuneWordId, LocalDate checkedAt) {

  public static UserFortune create(Long userId, Long fortuneWordId, LocalDate checkedAt) {
    return new UserFortune(null, userId, fortuneWordId, checkedAt);
  }

  public boolean needsRefresh(LocalDate today) {
    return !checkedAt.equals(today);
  }

  public UserFortune refreshed(Long newFortuneWordId, LocalDate today) {
    return new UserFortune(id, userId, newFortuneWordId, today);
  }
}
