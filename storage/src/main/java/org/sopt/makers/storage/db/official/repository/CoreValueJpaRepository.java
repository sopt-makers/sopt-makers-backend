package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.storage.db.official.entity.CoreValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoreValueJpaRepository extends JpaRepository<CoreValueEntity, Long> {

  List<CoreValueEntity> findByGenerationIdOrderByDisplayOrderAsc(Integer generationId);

  void deleteByGenerationId(Integer generationId);
}
