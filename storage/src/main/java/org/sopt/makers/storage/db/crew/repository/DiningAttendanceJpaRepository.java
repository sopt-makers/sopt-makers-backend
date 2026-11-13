package org.sopt.makers.storage.db.crew.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.dining.model.enums.DiningPayerStatus;
import org.sopt.makers.domain.crew.dining.model.projection.DiningAttendeeCountProjection;
import org.sopt.makers.storage.db.crew.entity.DiningAttendanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiningAttendanceJpaRepository extends JpaRepository<DiningAttendanceEntity, Long> {

  boolean existsByRoundIdAndUserId(Long roundId, Long userId);

  Optional<DiningAttendanceEntity> findByRoundIdAndUserId(Long roundId, Long userId);

  /** 결제자 상태 변경과 참석 삭제를 직렬화해 삭제 이력에 마지막 커밋 상태를 보관한다. */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM DiningAttendanceEntity a WHERE a.roundId = :roundId AND a.userId = :userId")
  Optional<DiningAttendanceEntity> findByRoundIdAndUserIdForUpdate(
      @Param("roundId") Long roundId, @Param("userId") Long userId);

  /** 회차·회식 삭제 시 스냅샷 생성 전에 잠금을 확보한다. 일괄 삭제끼리는 같은 ID 순서로 잠근다. */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM DiningAttendanceEntity a WHERE a.roundId IN :roundIds ORDER BY a.id ASC")
  List<DiningAttendanceEntity> findAllByRoundIdInForUpdate(@Param("roundIds") List<Long> roundIds);

  List<DiningAttendanceEntity> findAllByRoundIdInAndPayerStatusOrderByPayerRequestedAtAscIdAsc(
      List<Long> roundIds, DiningPayerStatus payerStatus);

  List<DiningAttendanceEntity> findAllByRoundIdIn(List<Long> roundIds);

  List<DiningAttendanceEntity> findAllByRoundId(Long roundId);

  List<DiningAttendanceEntity> findAllByRoundIdOrderByCreatedAtAscIdAsc(Long roundId);

  Page<DiningAttendanceEntity> findAllByRoundId(Long roundId, Pageable pageable);

  @Query("SELECT DISTINCT a.userId FROM DiningAttendanceEntity a WHERE a.roundId IN :roundIds")
  List<Long> findDistinctUserIdsByRoundIdIn(@Param("roundIds") List<Long> roundIds);

  long countByRoundId(Long roundId);

  boolean existsByRoundIdAndUserIdAndPayerStatus(
      Long roundId, Long userId, DiningPayerStatus payerStatus);

  /** 목록 카드용. 주어진 회식 중 사용자가 해당 결제자 상태인 회차가 있는 회식 id */
  @Query(
      "SELECT DISTINCT r.meetingId FROM DiningAttendanceEntity a, DiningRoundEntity r "
          + "WHERE a.roundId = r.id AND a.userId = :userId AND a.payerStatus = :payerStatus "
          + "AND r.meetingId IN :meetingIds")
  List<Long> findMeetingIdsByUserIdAndPayerStatus(
      @Param("userId") Long userId,
      @Param("payerStatus") DiningPayerStatus payerStatus,
      @Param("meetingIds") List<Long> meetingIds);

  @Query(
      "SELECT COUNT(DISTINCT a.userId) FROM DiningAttendanceEntity a, DiningRoundEntity r "
          + "WHERE a.roundId = r.id AND r.meetingId = :meetingId")
  long countDistinctUserIdsByMeetingId(@Param("meetingId") Long meetingId);

  /** 목록 카드용 고유 참석자 수 일괄 조회. 결과에 없는 회식의 참석자 수는 0으로 처리한다. */
  @Query(
      "SELECT r.meetingId AS meetingId, COUNT(DISTINCT a.userId) AS attendeeCount "
          + "FROM DiningAttendanceEntity a, DiningRoundEntity r "
          + "WHERE a.roundId = r.id AND r.meetingId IN :meetingIds GROUP BY r.meetingId")
  List<DiningAttendeeCountProjection> countDistinctUserIdsByMeetingIdIn(
      @Param("meetingIds") List<Long> meetingIds);
}
