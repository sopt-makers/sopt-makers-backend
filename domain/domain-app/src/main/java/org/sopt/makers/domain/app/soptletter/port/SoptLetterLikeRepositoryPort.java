package org.sopt.makers.domain.app.soptletter.port;

import java.util.Collection;
import java.util.Set;

public interface SoptLetterLikeRepositoryPort {

  boolean existsByLetterIdAndUserId(Long letterId, Long userId);

  Set<Long> findLikedLetterIds(Long userId, Collection<Long> letterIds);

  int addIfAbsent(Long userId, Long letterId);

  int deleteByLetterIdAndUserId(Long letterId, Long userId);

  void deleteAllByLetterId(Long letterId);
}
