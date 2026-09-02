package org.sopt.makers.storage.db.app.soptamp.stamp.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.app.soptamp.stamp.entity.ClapEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClapJpaRepository extends JpaRepository<ClapEntity, Long> {

  Optional<ClapEntity> findByStampIdAndUserId(Long stampId, Long userId);
}
