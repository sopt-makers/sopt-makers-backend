package org.sopt.makers.storage.db.app.soptletter.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterLikeNotificationRepositoryPort;
import org.sopt.makers.storage.db.app.soptletter.repository.SoptLetterLikeNotificationJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SoptLetterLikeNotificationRepositoryAdapter
    implements SoptLetterLikeNotificationRepositoryPort {

  private final SoptLetterLikeNotificationJpaRepository soptLetterLikeNotificationJpaRepository;

  @Override
  @Transactional
  public int addIfAbsent(Long userId, Long letterId) {
    return soptLetterLikeNotificationJpaRepository.addIfAbsent(userId, letterId);
  }
}
