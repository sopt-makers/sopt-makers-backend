package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandWaitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDemandWaitJpaRepository
    extends JpaRepository<MeetingDemandWaitEntity, Long> {

  boolean existsByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId);

  void deleteByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId);

  void deleteAllByMeetingDemandId(Long meetingDemandId);

  long countByMeetingDemandId(Long meetingDemandId);

  List<MeetingDemandWaitEntity> findAllByMeetingDemandIdInAndUserId(
      List<Long> meetingDemandIds, Long userId);

  List<MeetingDemandWaitEntity> findAllByMeetingDemandId(Long meetingDemandId);
}
