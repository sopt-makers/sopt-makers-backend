package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.storage.db.crew.entity.MeetingApplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingApplyJpaRepository extends JpaRepository<MeetingApplyEntity, Long> {

  List<MeetingApplyEntity> findAllByMeetingId(Long meetingId);

  List<MeetingApplyEntity> findAllByMeetingIdAndStatus(Long meetingId, MeetingApplyStatus status);

  List<MeetingApplyEntity> findAllByMeetingIdAndStatusIn(
      Long meetingId, List<MeetingApplyStatus> statuses);

  List<MeetingApplyEntity> findAllByUserIdAndStatus(Long userId, MeetingApplyStatus status);

  boolean existsByMeetingIdAndUserId(Long meetingId, Long userId);

  void deleteByMeetingIdAndUserId(Long meetingId, Long userId);

  void deleteAllByMeetingId(Long meetingId);
}
