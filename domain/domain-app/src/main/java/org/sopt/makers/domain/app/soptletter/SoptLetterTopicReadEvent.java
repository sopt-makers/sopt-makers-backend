package org.sopt.makers.domain.app.soptletter;

public record SoptLetterTopicReadEvent(Long userId, Long topicId, Long lastReadLetterId) {}
