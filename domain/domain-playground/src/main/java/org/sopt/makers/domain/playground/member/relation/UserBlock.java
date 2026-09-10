package org.sopt.makers.domain.playground.member.relation;

import java.time.LocalDateTime;

public record UserBlock(
    Long id,
    Long blockerUserId,
    Long blockedUserId,
    Boolean isBlocked,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
