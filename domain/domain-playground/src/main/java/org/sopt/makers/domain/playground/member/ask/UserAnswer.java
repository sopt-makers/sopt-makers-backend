package org.sopt.makers.domain.playground.member.ask;

import java.time.LocalDateTime;

public record UserAnswer(
    Long id, Long questionId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {}
