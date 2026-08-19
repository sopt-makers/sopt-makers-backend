package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.CoLeader;

public interface CoLeaderRepositoryPort {

  List<CoLeader> saveAll(List<CoLeader> coLeaders);

  List<CoLeader> findAllByMeetingId(Long meetingId);

  List<CoLeader> findAllByUserId(Long userId);

  boolean existsByMeetingIdAndUserId(Long meetingId, Long userId);

  void deleteAllByMeetingId(Long meetingId);
}
