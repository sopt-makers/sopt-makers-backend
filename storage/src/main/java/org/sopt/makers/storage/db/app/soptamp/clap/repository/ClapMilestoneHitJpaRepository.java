package org.sopt.makers.storage.db.app.soptamp.clap.repository;

import org.sopt.makers.storage.db.app.soptamp.clap.entity.ClapMilestoneHitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClapMilestoneHitJpaRepository
    extends JpaRepository<ClapMilestoneHitEntity, ClapMilestoneHitEntity.Id> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          "INSERT INTO clap_milestone_hit (stamp_id, milestone, created_at)"
              + " VALUES (:stampId, :milestone, now())"
              + " ON CONFLICT (stamp_id, milestone) DO NOTHING",
      nativeQuery = true)
  int insertIfAbsent(@Param("stampId") Long stampId, @Param("milestone") int milestone);
}
