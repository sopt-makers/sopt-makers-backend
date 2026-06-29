package org.sopt.makers.storage.db.official.repository;

import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.official.entity.RecruitPartIntroductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitPartIntroductionJpaRepository
    extends JpaRepository<RecruitPartIntroductionEntity, Long> {

  Optional<RecruitPartIntroductionEntity> findByGenerationIdAndPart(
      Integer generationId, Part part);
}
