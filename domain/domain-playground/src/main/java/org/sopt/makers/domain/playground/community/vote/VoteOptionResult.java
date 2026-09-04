package org.sopt.makers.domain.playground.community.vote;

public record VoteOptionResult(Long id, String content, int voteCount, int votePercent, boolean isSelected) {}
