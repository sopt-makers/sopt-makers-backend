package org.sopt.makers.domain.playground.member.relation;

import java.time.LocalDateTime;

public record UserReport(
    Long id,
    Long reporterUserId,
    Long reportedUserId,
    String reason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
