package org.sopt.makers.domain.playground.member.ask;

import java.time.LocalDateTime;

public record UserAsk(
    Long id,
    Long receiverUserId,
    Long askerUserId,
    String content,
    Boolean isAnonymous,
    Long anonymousNicknameId,
    Long anonymousProfileImageId,
    Boolean isReported,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
