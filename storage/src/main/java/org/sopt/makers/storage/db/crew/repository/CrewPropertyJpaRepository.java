package org.sopt.makers.storage.db.crew.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.CrewPropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewPropertyJpaRepository extends JpaRepository<CrewPropertyEntity, Long> {

  Optional<CrewPropertyEntity> findByKey(String key);
}
