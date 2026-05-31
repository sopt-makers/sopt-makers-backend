package org.sopt.makers.domain.user;

public enum UserSortType {
  LATEST_REGISTERED,
  OLDEST_REGISTERED,
  LATEST_GENERATION,
  OLDEST_GENERATION;

  public boolean isGenerationOrder() {
    return this == LATEST_GENERATION || this == OLDEST_GENERATION;
  }
}
