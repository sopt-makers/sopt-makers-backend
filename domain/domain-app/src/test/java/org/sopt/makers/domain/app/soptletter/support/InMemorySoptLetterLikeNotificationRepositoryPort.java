package org.sopt.makers.domain.app.soptletter.support;

import java.util.HashSet;
import java.util.Set;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterLikeNotificationRepositoryPort;

public final class InMemorySoptLetterLikeNotificationRepositoryPort
    implements SoptLetterLikeNotificationRepositoryPort {

  private final Set<String> notified = new HashSet<>();

  @Override
  public int addIfAbsent(Long userId, Long letterId) {
    return notified.add(letterId + ":" + userId) ? 1 : 0;
  }
}
