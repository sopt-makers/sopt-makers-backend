package org.sopt.makers.storage.db.crew.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandOpenedNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingDemandOpenedNotificationJpaRepository
    extends JpaRepository<MeetingDemandOpenedNotificationEntity, Long> {

  Optional<MeetingDemandOpenedNotificationEntity> findByMeetingId(Long meetingId);

  @Query(
      """
      SELECT notification.meetingId
      FROM MeetingDemandOpenedNotificationEntity notification
      JOIN MeetingEntity meeting ON meeting.id = notification.meetingId
      WHERE notification.sentAt IS NULL
        AND meeting.meetingDemandId IS NOT NULL
        AND meeting.startDate <= :now
        AND meeting.endDate > :now
      """)
  List<Long> findPendingMeetingIds(@Param("now") LocalDateTime now);
}
