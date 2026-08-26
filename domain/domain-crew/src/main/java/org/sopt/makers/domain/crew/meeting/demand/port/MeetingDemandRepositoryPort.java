package org.sopt.makers.domain.crew.meeting.demand.port;

import java.util.Optional;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;

public interface MeetingDemandRepositoryPort {

  MeetingDemand save(MeetingDemand meetingDemand);

  Optional<MeetingDemand> findById(Long meetingDemandId);

  Optional<MeetingDemand> findByIdForUpdate(Long meetingDemandId);

  PageResult<MeetingDemand> findAll(PageQuery pageQuery);

  void delete(MeetingDemand meetingDemand);
}
