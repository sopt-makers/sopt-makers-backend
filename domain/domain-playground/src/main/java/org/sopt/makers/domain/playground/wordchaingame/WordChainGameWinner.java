package org.sopt.makers.domain.playground.wordchaingame;

public record WordChainGameWinner(
    Long id,
    Long userId,
    Integer score,
    Long roomId) {}
