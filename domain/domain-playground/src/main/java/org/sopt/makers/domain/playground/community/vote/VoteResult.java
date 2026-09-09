package org.sopt.makers.domain.playground.community.vote;

import java.util.List;

/** 게시글 투표 결과 집계 read model. */
public record VoteResult(
    Long id,
    boolean isMultiple,
    boolean hasVoted,
    int totalParticipants,
    List<VoteOptionResult> options) {}
