package org.sopt.makers.domain.official.soptstory;

public enum SoptStorySortType {
  RECENT,
  LIKES;

  public static SoptStorySortType from(String value) {
    if ("likes".equalsIgnoreCase(value)) {
      return LIKES;
    }
    return RECENT;
  }
}
