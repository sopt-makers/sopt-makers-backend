package org.sopt.makers.api.controller.official.soptstory.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.official.soptstory.SoptStory;

public record SoptStoryResponse(
    Long id,
    String thumbnailUrl,
    String title,
    String description,
    String articleUrl,
    int likeCount,
    boolean isLiked,
    LocalDateTime createdAt) {

  public static SoptStoryResponse from(SoptStory soptStory, boolean isLiked) {
    return new SoptStoryResponse(
        soptStory.id(),
        soptStory.thumbnailUrl(),
        soptStory.title(),
        soptStory.description(),
        soptStory.url(),
        soptStory.likeCount(),
        isLiked,
        soptStory.createdAt());
  }
}
