package org.sopt.makers.domain.playground.member.ask;

import java.time.LocalDateTime;

public record AskReport(
    Long id, Long questionId, Long reporterUserId, String reason, LocalDateTime createdAt) {}
