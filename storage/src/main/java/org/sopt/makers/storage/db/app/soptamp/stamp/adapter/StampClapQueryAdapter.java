package org.sopt.makers.storage.db.app.soptamp.stamp.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampClapQueryPort;
import org.sopt.makers.storage.db.app.soptamp.stamp.repository.ClapJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StampClapQueryAdapter implements StampClapQueryPort {

  private final ClapJpaRepository clapJpaRepository;

  @Override
  public int getUserClapCount(Long userId, Long stampId) {
    return clapJpaRepository
        .findByStampIdAndUserId(stampId, userId)
        .map(clap -> clap.getClapCount())
        .orElse(0);
  }
}
