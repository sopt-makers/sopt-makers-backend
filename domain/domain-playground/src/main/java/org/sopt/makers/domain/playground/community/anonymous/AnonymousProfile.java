package org.sopt.makers.domain.playground.community.anonymous;

import java.time.LocalDateTime;

public record AnonymousProfile(
    Long id,
    Long userId,
    Long postId,
    AnonymousNickname nickname,
    AnonymousProfileImage profileImage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static AnonymousProfile create(
      Long userId, Long postId, AnonymousNickname nickname, AnonymousProfileImage profileImage) {
    return new AnonymousProfile(null, userId, postId, nickname, profileImage, null, null);
  }
}
