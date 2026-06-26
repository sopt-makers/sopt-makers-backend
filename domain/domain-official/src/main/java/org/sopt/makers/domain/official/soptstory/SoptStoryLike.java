package org.sopt.makers.domain.official.soptstory;

public record SoptStoryLike(Long id, Long soptStoryId, String ip) {

  public static SoptStoryLike create(Long soptStoryId, String ip) {
    return new SoptStoryLike(null, soptStoryId, ip);
  }
}
