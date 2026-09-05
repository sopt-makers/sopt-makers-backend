package org.sopt.makers.domain.playground.member.ask;

import java.time.LocalDateTime;

public record AskAnswerDetail(
    Long answerId,
    String content,
    long reactionCount,
    boolean isReacted,
    Long writerUserId,
    String writerName,
    String writerProfileImage,
    LocalDateTime createdAt) {}
