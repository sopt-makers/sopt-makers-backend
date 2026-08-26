package org.sopt.makers.domain.crew.meeting.demand.port;

import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingDemandRepositoryPort {

  MeetingDemand save(MeetingDemand meetingDemand);

  Optional<MeetingDemand> findById(Long meetingDemandId);

  Optional<MeetingDemand> findByIdForUpdate(Long meetingDemandId);

  Page<MeetingDemand> findAll(Pageable pageable);

  void delete(MeetingDemand meetingDemand);
}
