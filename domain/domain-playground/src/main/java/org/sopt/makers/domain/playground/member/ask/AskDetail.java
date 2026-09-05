package org.sopt.makers.domain.playground.member.ask;

import java.time.LocalDateTime;

public record AskDetail(
    Long questionId,
    String content,
    Long askerUserId,
    String askerName,
    String askerProfileImage,
    String askerLatestGenerationLabel,
    String anonymousNickname,
    String anonymousProfileImageUrl,
    Boolean isAnonymous,
    long reactionCount,
    boolean isReacted,
    boolean isAnswered,
    AskAnswerDetail answer,
    LocalDateTime createdAt,
    boolean isNew,
    boolean isMine,
    boolean isReceived) {}
