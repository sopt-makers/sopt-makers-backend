package org.sopt.makers.storage.db.app.soptamp.clap.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.app.soptamp.clap.entity.ClapEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClapJpaRepository extends JpaRepository<ClapEntity, Long> {

  Optional<ClapEntity> findByUserIdAndStampId(Long userId, Long stampId);

  List<ClapEntity> findAllByStampIdOrderByClapCountDescUpdatedAtDesc(
      Long stampId, Pageable pageable);

  long countByStampId(Long stampId);

  List<ClapEntity> findAllByUserId(Long userId);
}
