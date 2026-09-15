package org.sopt.makers.domain.playground.community.vote;

import java.time.LocalDateTime;
import java.util.List;

public record Vote(
    Long id,
    Long postId,
    boolean isMultipleOptions,
    List<VoteOption> options,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static Vote create(Long postId, boolean isMultipleOptions, List<String> optionContents) {
    List<VoteOption> options = optionContents.stream().map(VoteOption::create).toList();
    return new Vote(null, postId, isMultipleOptions, options, null, null);
  }
}
