package org.sopt.makers.domain.crew.meeting.port;

import java.util.Optional;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.Meeting;

public interface MeetingRepositoryPort {

  Meeting save(Meeting meeting);

  Optional<Meeting> findById(Long meetingId);

  PageResult<Meeting> findAll(PageQuery pageQuery);

  PageResult<Meeting> findAllByUserId(Long userId, PageQuery pageQuery);

  PageResult<Meeting> findAllByMeetingDemandId(Long meetingDemandId, PageQuery pageQuery);

  long countByMeetingDemandId(Long meetingDemandId);

  void clearMeetingDemandId(Long meetingDemandId);

  void delete(Meeting meeting);
}
