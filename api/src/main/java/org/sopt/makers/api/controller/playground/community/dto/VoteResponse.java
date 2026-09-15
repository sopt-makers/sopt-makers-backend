package org.sopt.makers.api.controller.playground.community.dto;

import java.util.List;
import org.sopt.makers.domain.playground.community.vote.VoteResult;

public record VoteResponse(
    Long id,
    boolean isMultiple,
    boolean hasVoted,
    int totalParticipants,
    List<VoteOptionResponse> options) {

  public static VoteResponse from(VoteResult result) {
    return new VoteResponse(
        result.id(),
        result.isMultiple(),
        result.hasVoted(),
        result.totalParticipants(),
        result.options().stream().map(VoteOptionResponse::from).toList());
  }
}
