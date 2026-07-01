package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.storage.db.official.entity.ActivityScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityScheduleJpaRepository extends JpaRepository<ActivityScheduleEntity, Long> {

  List<ActivityScheduleEntity> findByGenerationIdOrderByStartDateAsc(Integer generationId);

  void deleteByGenerationId(Integer generationId);
}
