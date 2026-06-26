package org.sopt.makers.storage.db.admin.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.storage.db.admin.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, Long> {

  @Query(
      "SELECT s FROM ScheduleEntity s"
          + " WHERE s.startDate <= :endAt AND s.endDate >= :startAt"
          + " ORDER BY s.startDate ASC")
  List<ScheduleEntity> findBetween(
      @Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);
}
