package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;
import java.util.List;

public record Post(
    Long id,
    Long writerId,
    Long categoryId,
    String title,
    String content,
    Integer hits,
    List<String> images,
    Boolean isQuestion,
    Boolean isBlindWriter,
    Boolean isReported,
    Boolean isHot,
    String sopticleUrl,
    Long anonymousProfileId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static Post create(
      Long writerId,
      Long categoryId,
      String title,
      String content,
      List<String> images,
      Boolean isQuestion,
      Boolean isBlindWriter,
      String sopticleUrl) {
    return new Post(
        null,
        writerId,
        categoryId,
        title,
        content,
        0,
        images,
        isQuestion,
        isBlindWriter,
        false,
        false,
        sopticleUrl,
        null,
        null,
        null);
  }

  public Post update(
      Long categoryId,
      String title,
      String content,
      List<String> images,
      Boolean isBlindWriter,
      String sopticleUrl) {
    return new Post(
        id,
        writerId,
        categoryId,
        title,
        content,
        hits,
        images,
        false,
        isBlindWriter,
        isReported,
        isHot,
        sopticleUrl,
        anonymousProfileId,
        createdAt,
        updatedAt);
  }

  public Post withAnonymousProfileId(Long anonymousProfileId) {
    return new Post(
        id,
        writerId,
        categoryId,
        title,
        content,
        hits,
        images,
        isQuestion,
        isBlindWriter,
        isReported,
        isHot,
        sopticleUrl,
        anonymousProfileId,
        createdAt,
        updatedAt);
  }
}
