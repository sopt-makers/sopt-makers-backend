package org.sopt.makers.domain.app.soptletter;

public record SoptLetterLikeEvent(
    Long likerUserId, Long authorUserId, Long topicId, Long letterId) {}
