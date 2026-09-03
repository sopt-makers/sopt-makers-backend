package org.sopt.makers.storage.db.playground.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.post.entity.MumuTextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MumuTextJpaRepository extends JpaRepository<MumuTextEntity, Long> {

  Optional<MumuTextEntity>
      findFirstByShowStartDateLessThanEqualAndShowEndDateGreaterThanOrderByShowStartDateDesc(
          LocalDateTime startDate, LocalDateTime endDate);

  List<MumuTextEntity> findAllByOrderByShowStartDateAscIdAsc();

  @Query(
      "SELECT text FROM MumuTextEntity text "
          + "WHERE (:excludedId IS NULL OR text.id <> :excludedId) "
          + "AND text.showStartDate < :endDate AND text.showEndDate > :startDate")
  List<MumuTextEntity> findOverlapping(
      @Param("excludedId") Long excludedId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
