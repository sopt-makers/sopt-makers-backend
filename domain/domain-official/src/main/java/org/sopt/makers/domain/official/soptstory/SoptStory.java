package org.sopt.makers.domain.official.soptstory;

import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.INVALID_LIKE_COUNT;

import java.time.LocalDateTime;
import org.sopt.makers.domain.official.soptstory.exception.SoptStoryException;

public record SoptStory(
    Long id,
    String title,
    String description,
    String thumbnailUrl,
    String url,
    int likeCount,
    LocalDateTime createdAt) {

  public static SoptStory create(
      String title, String description, String thumbnailUrl, String url) {
    return new SoptStory(null, title, description, thumbnailUrl, url, 0, null);
  }

  public SoptStory incrementLike() {
    return new SoptStory(id, title, description, thumbnailUrl, url, likeCount + 1, createdAt);
  }

  public SoptStory decrementLike() {
    if (likeCount <= 0) {
      throw new SoptStoryException(INVALID_LIKE_COUNT);
    }
    return new SoptStory(id, title, description, thumbnailUrl, url, likeCount - 1, createdAt);
  }
}
