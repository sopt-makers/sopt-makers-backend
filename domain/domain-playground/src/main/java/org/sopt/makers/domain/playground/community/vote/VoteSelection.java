package org.sopt.makers.domain.playground.community.vote;

import java.time.LocalDateTime;

public record VoteSelection(Long id, Long userId, Long voteOptionId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static VoteSelection create(Long userId, Long voteOptionId) {
    return new VoteSelection(null, userId, voteOptionId, null, null);
  }
}
