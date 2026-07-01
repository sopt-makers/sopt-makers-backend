package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.storage.db.official.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

  List<MemberEntity> findByGenerationIdOrderByRoleAsc(Integer generationId);

  void deleteByGenerationId(Integer generationId);
}
