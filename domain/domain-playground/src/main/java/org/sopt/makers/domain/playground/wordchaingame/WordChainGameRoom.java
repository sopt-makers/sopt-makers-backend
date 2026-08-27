package org.sopt.makers.domain.playground.wordchaingame;

import java.time.LocalDateTime;
import java.util.List;

public record WordChainGameRoom(
    Long id,
    String startWord,
    LocalDateTime createdAt,
    Long createdUserId,
    List<Word> wordList) {}
