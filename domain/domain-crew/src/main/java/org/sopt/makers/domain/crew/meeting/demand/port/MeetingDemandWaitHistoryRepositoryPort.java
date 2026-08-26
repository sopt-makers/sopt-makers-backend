package org.sopt.makers.domain.crew.meeting.demand.port;

import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandWaitHistory;

public interface MeetingDemandWaitHistoryRepositoryPort {

  MeetingDemandWaitHistory save(MeetingDemandWaitHistory history);

  boolean existsByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId);

  void deleteAllByMeetingDemandId(Long meetingDemandId);
}
