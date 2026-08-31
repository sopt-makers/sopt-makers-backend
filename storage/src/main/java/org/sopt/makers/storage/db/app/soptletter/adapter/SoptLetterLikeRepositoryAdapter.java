package org.sopt.makers.storage.db.app.soptletter.adapter;

import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterLikeRepositoryPort;
import org.sopt.makers.storage.db.app.soptletter.repository.SoptLetterLikeJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptLetterLikeRepositoryAdapter implements SoptLetterLikeRepositoryPort {

  private final SoptLetterLikeJpaRepository soptLetterLikeJpaRepository;

  @Override
  public boolean existsByLetterIdAndUserId(Long letterId, Long userId) {
    return soptLetterLikeJpaRepository.existsByLetterIdAndUserId(letterId, userId);
  }

  @Override
  public Set<Long> findLikedLetterIds(Long userId, Collection<Long> letterIds) {
    if (letterIds.isEmpty()) {
      return Set.of();
    }
    return Set.copyOf(soptLetterLikeJpaRepository.findLikedLetterIds(userId, letterIds));
  }

  @Override
  @Transactional
  public int addIfAbsent(Long userId, Long letterId) {
    return soptLetterLikeJpaRepository.addIfAbsent(userId, letterId);
  }

  @Override
  @Transactional
  public int deleteByLetterIdAndUserId(Long letterId, Long userId) {
    return soptLetterLikeJpaRepository.deleteByLetterIdAndUserId(letterId, userId);
  }

  @Override
  @Transactional
  public void deleteAllByLetterId(Long letterId) {
    soptLetterLikeJpaRepository.deleteAllByLetterId(letterId);
  }
}
