package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingSearchCondition;

public interface MeetingRepositoryPort {

  Meeting save(Meeting meeting);

  Optional<Meeting> findById(Long meetingId);

  Optional<Meeting> findByIdForUpdate(Long meetingId);

  List<Meeting> findAllByIds(List<Long> meetingIds);

  Optional<Long> findFirstIdByTitle(String title);

  Optional<Long> findFirstIdByTitleContaining(String title);

  PageResult<Meeting> findAll(PageQuery pageQuery);

  PageResult<Meeting> search(MeetingSearchCondition condition, PageQuery pageQuery);

  PageResult<Meeting> findAllByLeaderUserId(Long userId, PageQuery pageQuery);

  PageResult<Meeting> findAllByMemberUserId(Long userId, PageQuery pageQuery);

  PageResult<Meeting> findAllByMeetingDemandId(Long meetingDemandId, PageQuery pageQuery);

  long countByMeetingDemandId(Long meetingDemandId);

  void clearMeetingDemandId(Long meetingDemandId);

  void delete(Meeting meeting);
}
