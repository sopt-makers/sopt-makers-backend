package org.sopt.makers.domain.crew.meeting.port;

import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingRepositoryPort {

  Meeting save(Meeting meeting);

  Optional<Meeting> findById(Long meetingId);

  Page<Meeting> findAll(Pageable pageable);

  Page<Meeting> findAllByUserId(Long userId, Pageable pageable);

  Page<Meeting> findAllByMeetingDemandId(Long meetingDemandId, Pageable pageable);

  long countByMeetingDemandId(Long meetingDemandId);

  void clearMeetingDemandId(Long meetingDemandId);

  void delete(Meeting meeting);
}
