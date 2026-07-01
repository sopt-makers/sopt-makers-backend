package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.storage.db.official.entity.RecruitmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentJpaRepository extends JpaRepository<RecruitmentEntity, Long> {

  List<RecruitmentEntity> findByGenerationIdOrderByRecruitTypeAsc(Integer generationId);

  void deleteByGenerationId(Integer generationId);
}
