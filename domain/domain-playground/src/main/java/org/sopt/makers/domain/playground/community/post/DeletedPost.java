package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;
import java.util.List;

public record DeletedPost(
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
    LocalDateTime deletedAt) {

  public static DeletedPost from(Post post) {
    return new DeletedPost(
        null,
        post.writerId(),
        post.categoryId(),
        post.title(),
        post.content(),
        post.hits(),
        post.images(),
        post.isQuestion(),
        post.isBlindWriter(),
        post.isReported(),
        LocalDateTime.now());
  }
}
