package org.sopt.makers.domain.app.soptletter.port;

public interface SoptLetterTopicReadRepositoryPort {

  void upsertLastReadLetterId(Long userId, Long topicId, Long lastReadLetterId);
}
