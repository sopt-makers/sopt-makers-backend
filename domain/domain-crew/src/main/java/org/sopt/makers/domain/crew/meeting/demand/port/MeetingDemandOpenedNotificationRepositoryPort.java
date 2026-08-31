package org.sopt.makers.domain.crew.meeting.demand.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandOpenedNotification;

public interface MeetingDemandOpenedNotificationRepositoryPort {

  MeetingDemandOpenedNotification save(MeetingDemandOpenedNotification notification);

  Optional<MeetingDemandOpenedNotification> findById(Long notificationId);

  Optional<MeetingDemandOpenedNotification> findByMeetingId(Long meetingId);

  List<Long> findPendingMeetingIds(LocalDateTime now);
}
