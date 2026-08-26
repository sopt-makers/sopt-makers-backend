package org.sopt.makers.storage.db.crew.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.SoptMapPropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptMapPropertyJpaRepository extends JpaRepository<SoptMapPropertyEntity, Long> {

  Optional<SoptMapPropertyEntity> findByKey(String key);
}
