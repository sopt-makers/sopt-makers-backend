package org.sopt.makers.storage.db.crew.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.dining.model.enums.DiningRoundStatus;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.storage.db.crew.entity.DiningRoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiningRoundJpaRepository extends JpaRepository<DiningRoundEntity, Long> {

  // 모든 회식 변경과 같은 부모 잠금 순서를 사용해 자동 마감과 수정의 덮어쓰기를 막는다.
  @Query(
      value =
          "SELECT id FROM meeting WHERE category = 'DINING' AND activity_start_date < :today ORDER BY id FOR UPDATE",
      nativeQuery = true)
  List<Long> lockExpiredDinings(@Param("today") LocalDateTime today);

  /**
   * @implSpec : 회식의 회차를 1차부터 순서대로 조회한다. (인덱스 + 1 = 회차 번호)
   */
  List<DiningRoundEntity> findAllByMeetingIdOrderByIdAsc(Long meetingId);

  List<DiningRoundEntity> findAllByMeetingIdInOrderByIdAsc(List<Long> meetingIds);

  /**
   * @implSpec : 회차·회식 삭제 시 스냅샷 생성 전에 회차를 id 순서로 먼저 잠근다. 참석 추가 INSERT의 FK 검사(KEY SHARE)와 충돌해 삭제 중
   *     들어온 참석이 이력 없이 CASCADE 삭제되지 않는다. Hibernate 6의 PESSIMISTIC_WRITE는 PostgreSQL에서 FOR NO KEY
   *     UPDATE로 나가 KEY SHARE와 충돌하지 않으므로 native query로 FOR UPDATE를 명시한다.
   */
  @Query(
      value = "SELECT * FROM dining_round WHERE meeting_id = :meetingId ORDER BY id FOR UPDATE",
      nativeQuery = true)
  List<DiningRoundEntity> findAllByMeetingIdForUpdate(@Param("meetingId") Long meetingId);

  /**
   * @implSpec : 자동 확정. 회식 일자가 기준일(today) 이전인 회식의 미마감 회차를 한 번에 마감한다.
   */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE DiningRoundEntity r SET r.status = :closed, r.closedAt = :now, r.updatedAt = :now "
          + "WHERE r.status = :open AND r.meetingId IN "
          + "(SELECT m.id FROM MeetingEntity m WHERE m.category = :category AND m.activityStartDate < :today)")
  int closeOpenRoundsOfDiningsBefore(
      @Param("closed") DiningRoundStatus closed,
      @Param("open") DiningRoundStatus open,
      @Param("category") MeetingCategory category,
      @Param("today") LocalDateTime today,
      @Param("now") LocalDateTime now);
}
