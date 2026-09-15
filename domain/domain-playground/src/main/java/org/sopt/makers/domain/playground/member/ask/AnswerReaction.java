package org.sopt.makers.domain.playground.member.ask;

import java.time.LocalDateTime;

public record AnswerReaction(
    Long id, Long answerId, Long reactorUserId, LocalDateTime createdAt, LocalDateTime updatedAt) {}
