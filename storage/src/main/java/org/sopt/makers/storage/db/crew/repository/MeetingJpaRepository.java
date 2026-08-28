package org.sopt.makers.storage.db.crew.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingJpaRepository extends JpaRepository<MeetingEntity, Long> {

  int countAllByCreatedGeneration(Integer createdGeneration);

  Optional<MeetingEntity> findFirstByTitleOrderByIdDesc(String title);

  Optional<MeetingEntity> findFirstByTitleContainingOrderByIdDesc(String title);

  Page<MeetingEntity> findAllByUserId(Long userId, Pageable pageable);

  Page<MeetingEntity> findAllByMeetingDemandId(Long meetingDemandId, Pageable pageable);

  long countByMeetingDemandId(Long meetingDemandId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE MeetingEntity meeting SET meeting.meetingDemandId = NULL WHERE meeting.meetingDemandId = :meetingDemandId")
  void clearMeetingDemandId(@Param("meetingDemandId") Long meetingDemandId);
}
