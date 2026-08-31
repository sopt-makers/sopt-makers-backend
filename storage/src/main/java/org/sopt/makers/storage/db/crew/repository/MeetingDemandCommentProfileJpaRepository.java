package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandCommentProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDemandCommentProfileJpaRepository
    extends JpaRepository<MeetingDemandCommentProfileEntity, Long> {

  Optional<MeetingDemandCommentProfileEntity> findByMeetingDemandIdAndUserId(
      Long meetingDemandId, Long userId);

  List<MeetingDemandCommentProfileEntity> findAllByMeetingDemandIdAndUserIdIn(
      Long meetingDemandId, List<Long> userIds);

  void deleteAllByMeetingDemandId(Long meetingDemandId);
}
