package org.sopt.makers.domain.crew.meeting.demand.port;

import java.util.List;
import java.util.Set;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandWait;

public interface MeetingDemandWaitRepositoryPort {

  MeetingDemandWait save(MeetingDemandWait wait);

  boolean existsByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId);

  void deleteByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId);

  void deleteAllByMeetingDemandId(Long meetingDemandId);

  long countByMeetingDemandId(Long meetingDemandId);

  Set<Long> findMeetingDemandIdsByUserIdAndMeetingDemandIds(
      Long userId, List<Long> meetingDemandIds);

  List<Long> findUserIdsByMeetingDemandId(Long meetingDemandId);
}
