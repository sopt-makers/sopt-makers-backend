package org.sopt.makers.storage.db.app.soptamp.clap.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapRepositoryPort;
import org.sopt.makers.storage.db.app.soptamp.clap.entity.ClapEntity;
import org.sopt.makers.storage.db.app.soptamp.clap.repository.ClapJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClapRepositoryAdapter implements ClapRepositoryPort {

  private final ClapJpaRepository clapJpaRepository;

  @Override
  public Optional<Clap> findByUserIdAndStampId(Long userId, Long stampId) {
    return clapJpaRepository.findByUserIdAndStampId(userId, stampId).map(ClapEntity::toDomain);
  }

  @Override
  public List<Clap> findAllByStampIdOrderByClapCountDesc(Long stampId, Pageable pageable) {
    return toDomains(
        clapJpaRepository.findAllByStampIdOrderByClapCountDescUpdatedAtDesc(stampId, pageable));
  }

  @Override
  public long countByStampId(Long stampId) {
    return clapJpaRepository.countByStampId(stampId);
  }

  @Override
  public List<Clap> findAllByUserId(Long userId) {
    return toDomains(clapJpaRepository.findAllByUserId(userId));
  }

  @Override
  @Transactional
  public Clap save(Clap clap) {
    return clapJpaRepository.saveAndFlush(ClapEntity.from(clap)).toDomain();
  }

  @Override
  @Transactional
  public void deleteAll() {
    clapJpaRepository.deleteAllInBatch();
  }

  private List<Clap> toDomains(List<ClapEntity> entities) {
    return entities.stream().map(ClapEntity::toDomain).toList();
  }
}
