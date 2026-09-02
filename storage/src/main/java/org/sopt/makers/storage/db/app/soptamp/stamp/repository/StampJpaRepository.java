package org.sopt.makers.storage.db.app.soptamp.stamp.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.app.soptamp.stamp.entity.StampEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StampJpaRepository extends JpaRepository<StampEntity, Long> {

  Optional<StampEntity> findByIdAndUserId(Long id, Long userId);

  Optional<StampEntity> findByUserIdAndMissionId(Long userId, Long missionId);

  List<StampEntity> findAllByUserId(Long userId);

  List<StampEntity> findAllByUserIdIn(Collection<Long> userIds);

  boolean existsByUserIdInAndMissionId(Collection<Long> userIds, Long missionId);

  void deleteAllByUserId(Long userId);

  @Query(
      "SELECT s FROM StampEntity s JOIN MissionEntity m ON s.missionId = m.id"
          + " WHERE m.display = true ORDER BY s.createdAt DESC")
  List<StampEntity> findAllByMissionDisplayTrueOrderByCreatedAtDesc(Pageable pageable);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE StampEntity s SET s.viewCount = s.viewCount + 1 WHERE s.id = :stampId")
  void increaseViewCount(@Param("stampId") Long stampId);

  @Query(
      value =
          """
          SELECT s.user_id AS userId, SUM(m.level) AS todayPoints, MIN(s.created_at) AS firstCertifiedAtToday
          FROM stamp s JOIN mission m ON s.mission_id = m.mission_id
          WHERE m.display = true AND s.created_at >= :todayStart AND s.created_at < :tomorrowStart
          GROUP BY s.user_id
          ORDER BY todayPoints DESC, firstCertifiedAtToday ASC
          """,
      nativeQuery = true)
  List<TodayUserRankRow> findTodayUserRankSources(
      @Param("todayStart") LocalDateTime todayStart,
      @Param("tomorrowStart") LocalDateTime tomorrowStart);

  interface TodayUserRankRow {
    Long getUserId();

    long getTodayPoints();

    LocalDateTime getFirstCertifiedAtToday();
  }
}
