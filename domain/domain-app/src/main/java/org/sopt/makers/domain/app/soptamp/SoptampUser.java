package org.sopt.makers.domain.app.soptamp;

public record SoptampUser(
    Long id, Long userId, String nickname, String profileMessage, Long totalPoints, String part) {}
