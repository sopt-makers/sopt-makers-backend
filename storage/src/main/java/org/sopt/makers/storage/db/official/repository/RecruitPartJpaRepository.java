package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.official.entity.RecruitPartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitPartJpaRepository extends JpaRepository<RecruitPartEntity, Long> {

  List<RecruitPartEntity> findByGenerationIdOrderByPartTypeAsc(Integer generationId);

  Optional<RecruitPartEntity> findByGenerationIdAndPartType(Integer generationId, Part partType);

  void deleteByGenerationId(Integer generationId);
}
