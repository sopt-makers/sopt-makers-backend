package org.sopt.makers.storage.db.playground.report.repository;

import java.time.LocalDateTime;
import org.sopt.makers.storage.db.playground.report.entity.WordChainGameWinnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportWordChainGameWinnerJpaRepository
    extends JpaRepository<WordChainGameWinnerEntity, Long> {

  @Query(
      """
      SELECT COUNT(winner)
      FROM ReportWordChainGameWinnerEntity winner
      JOIN ReportWordChainGameRoomEntity room ON room.id = winner.roomId
      WHERE winner.userId = :userId
        AND room.createdAt BETWEEN :start AND :end
      """)
  long countByUserIdAndRoomCreatedAtBetween(
      @Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);
}
