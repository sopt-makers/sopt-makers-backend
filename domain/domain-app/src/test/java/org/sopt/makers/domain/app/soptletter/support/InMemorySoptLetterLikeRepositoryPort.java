package org.sopt.makers.domain.app.soptletter.support;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterLikeRepositoryPort;

public final class InMemorySoptLetterLikeRepositoryPort implements SoptLetterLikeRepositoryPort {

  private record Like(Long userId, Long letterId) {}

  private final Set<Like> store = new LinkedHashSet<>();

  @Override
  public boolean existsByLetterIdAndUserId(Long letterId, Long userId) {
    return store.contains(new Like(userId, letterId));
  }

  @Override
  public Set<Long> findLikedLetterIds(Long userId, Collection<Long> letterIds) {
    return store.stream()
        .filter(like -> like.userId().equals(userId) && letterIds.contains(like.letterId()))
        .map(Like::letterId)
        .collect(Collectors.toSet());
  }

  @Override
  public int addIfAbsent(Long userId, Long letterId) {
    return store.add(new Like(userId, letterId)) ? 1 : 0;
  }

  @Override
  public int deleteByLetterIdAndUserId(Long letterId, Long userId) {
    return store.remove(new Like(userId, letterId)) ? 1 : 0;
  }

  @Override
  public void deleteAllByLetterId(Long letterId) {
    store.removeIf(like -> like.letterId().equals(letterId));
  }
}
