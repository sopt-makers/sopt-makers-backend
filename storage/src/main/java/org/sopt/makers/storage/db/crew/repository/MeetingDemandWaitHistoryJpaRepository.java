package org.sopt.makers.storage.db.crew.repository;

import org.sopt.makers.storage.db.crew.entity.MeetingDemandWaitHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDemandWaitHistoryJpaRepository
    extends JpaRepository<MeetingDemandWaitHistoryEntity, Long> {

  boolean existsByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId);

  void deleteAllByMeetingDemandId(Long meetingDemandId);
}
