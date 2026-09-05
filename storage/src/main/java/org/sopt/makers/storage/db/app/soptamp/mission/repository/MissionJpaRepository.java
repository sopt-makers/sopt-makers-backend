package org.sopt.makers.storage.db.app.soptamp.mission.repository;

import java.util.List;
import org.sopt.makers.storage.db.app.soptamp.mission.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionJpaRepository extends JpaRepository<MissionEntity, Long> {

  List<MissionEntity> findAllByDisplay(boolean display);

  List<MissionEntity> findAllByDisplayOrderByLevelAscTitleAsc(boolean display);

  @Query(
      "SELECT m FROM MissionEntity m WHERE m.id IN :missionIds ORDER BY m.level ASC, m.title ASC")
  List<MissionEntity> findByIdInOrderByLevelAscTitleAsc(@Param("missionIds") List<Long> missionIds);

  @Query(
      "SELECT m FROM MissionEntity m WHERE m.id IN :missionIds AND m.display = true"
          + " ORDER BY m.level ASC, m.title ASC")
  List<MissionEntity> findDisplayedByIdInOrderByLevelAscTitleAsc(
      @Param("missionIds") List<Long> missionIds);
}
