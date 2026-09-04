package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.domain.playground.community.vote.VoteOptionResult;

public record VoteOptionResponse(Long id, String content, int voteCount, int votePercent, boolean isSelected) {

  public static VoteOptionResponse from(VoteOptionResult result) {
    return new VoteOptionResponse(
        result.id(), result.content(), result.voteCount(), result.votePercent(), result.isSelected());
  }
}
