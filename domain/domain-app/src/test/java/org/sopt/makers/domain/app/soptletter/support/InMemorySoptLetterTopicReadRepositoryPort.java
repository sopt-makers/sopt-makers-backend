package org.sopt.makers.domain.app.soptletter.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterTopicReadRepositoryPort;

public final class InMemorySoptLetterTopicReadRepositoryPort
    implements SoptLetterTopicReadRepositoryPort {

  private final Map<String, Long> lastReadLetterIds = new HashMap<>();

  @Override
  public void upsertLastReadLetterId(Long userId, Long topicId, Long lastReadLetterId) {
    lastReadLetterIds.merge(key(userId, topicId), lastReadLetterId, Math::max);
  }

  public Long lastReadLetterId(Long userId, Long topicId) {
    return lastReadLetterIds.get(key(userId, topicId));
  }

  Optional<Long> findLastReadLetterId(Long userId, Long topicId) {
    return Optional.ofNullable(lastReadLetterId(userId, topicId));
  }

  private String key(Long userId, Long topicId) {
    return userId + ":" + topicId;
  }
}
