package org.sopt.makers.domain.playground.wordchaingame;

import java.time.LocalDateTime;

public record Word(
    Long id,
    Long memberId,
    String word,
    Long roomId,
    LocalDateTime createdAt) {}
