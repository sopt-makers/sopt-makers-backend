package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.storage.db.official.entity.ExecutiveMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutiveMemberJpaRepository extends JpaRepository<ExecutiveMemberEntity, Long> {

  List<ExecutiveMemberEntity> findByGenerationIdOrderByRoleAsc(Integer generationId);

  void deleteByGenerationId(Integer generationId);
}
