package org.sopt.makers.storage.db.user.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserActivityHistoryJpaRepository
    extends JpaRepository<UserActivityHistoryEntity, Long> {

  List<UserActivityHistoryEntity> findByUserId(Long userId);

  List<UserActivityHistoryEntity> findAllByUserIdIn(List<Long> userIds);

  Optional<UserActivityHistoryEntity> findFirstByUserIdOrderByGenerationDesc(Long userId);

  void deleteByUserId(Long userId);

  List<UserActivityHistoryEntity> findByGenerationAndIsSopt(int generation, boolean isSopt);

  List<UserActivityHistoryEntity> findByGenerationAndPartAndIsSopt(
      int generation, Part part, boolean isSopt);

  @Query(
      "SELECT a FROM UserActivityHistoryEntity a JOIN FETCH a.user"
          + " WHERE a.generation = :generation AND a.isSopt = true"
          + " AND (:part IS NULL OR a.part = :part OR :part = org.sopt.makers.core.type.Part.ALL)")
  List<UserActivityHistoryEntity> findByGenerationAndPartWithUser(
      @Param("generation") int generation, @Param("part") Part part, Pageable pageable);

  @Query(
      "SELECT COUNT(a) FROM UserActivityHistoryEntity a"
          + " WHERE a.generation = :generation AND a.isSopt = true"
          + " AND (:part IS NULL OR a.part = :part OR :part = org.sopt.makers.core.type.Part.ALL)")
  int countByGenerationAndPart(@Param("generation") int generation, @Param("part") Part part);

  @Query(
      "SELECT COUNT(DISTINCT a.user.id) FROM UserActivityHistoryEntity a"
          + " WHERE a.generation = :generation AND a.isSopt = :isSopt")
  int countDistinctUserByGenerationAndIsSopt(
      @Param("generation") int generation, @Param("isSopt") boolean isSopt);

  @Query(
      "SELECT a.user.id FROM UserActivityHistoryEntity a"
          + " WHERE a.generation = :generation AND a.isSopt = true"
          + " AND (:part IS NULL OR a.part = :part OR :part = org.sopt.makers.core.type.Part.ALL)")
  List<Long> findUserIdsByGenerationAndPart(
      @Param("generation") int generation, @Param("part") Part part);

  @Modifying(clearAutomatically = true)
  @Query(
      "UPDATE UserActivityHistoryEntity u SET u.attendanceScore = :score"
          + " WHERE u.user.id = :userId AND u.generation = :generation")
  void updateAttendanceScore(
      @Param("userId") Long userId,
      @Param("generation") int generation,
      @Param("score") Float score);
}
