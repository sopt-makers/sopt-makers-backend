package org.sopt.makers.storage.db.app.soptamp.clap.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapMilestonePort;
import org.sopt.makers.storage.db.app.soptamp.clap.repository.ClapMilestoneHitJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class ClapMilestoneAdapter implements ClapMilestonePort {

  private final ClapMilestoneHitJpaRepository clapMilestoneHitJpaRepository;

  @Override
  public boolean tryMarkFirstHit(Long stampId, int milestone) {
    return clapMilestoneHitJpaRepository.insertIfAbsent(stampId, milestone) == 1;
  }

  @Override
  public void deleteAll() {
    clapMilestoneHitJpaRepository.deleteAllInBatch();
  }
}
