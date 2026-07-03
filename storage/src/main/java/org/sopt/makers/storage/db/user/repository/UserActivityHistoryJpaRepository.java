package org.sopt.makers.storage.db.user.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
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
      "SELECT COUNT(DISTINCT a.user.id) FROM UserActivityHistoryEntity a"
          + " WHERE a.generation = :generation AND a.isSopt = :isSopt")
  int countDistinctUserByGenerationAndIsSopt(
      @Param("generation") int generation, @Param("isSopt") boolean isSopt);

  @Modifying(clearAutomatically = true)
  @Query(
      "UPDATE UserActivityHistoryEntity u SET u.attendanceScore = :score"
          + " WHERE u.user.id = :userId AND u.generation = :generation")
  void updateAttendanceScore(
      @Param("userId") Long userId,
      @Param("generation") int generation,
      @Param("score") Float score);
}
