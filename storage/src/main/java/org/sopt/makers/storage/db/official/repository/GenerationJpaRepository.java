package org.sopt.makers.storage.db.official.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.official.entity.GenerationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerationJpaRepository extends JpaRepository<GenerationEntity, Integer> {

  Optional<GenerationEntity> findFirstByOrderByIdDesc();
}
