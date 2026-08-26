package org.sopt.makers.domain.crew.meeting.demand.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentProfile;

public interface MeetingDemandCommentProfileRepositoryPort {

  MeetingDemandCommentProfile save(MeetingDemandCommentProfile profile);

  Optional<MeetingDemandCommentProfile> findByMeetingDemandIdAndUserId(
      Long meetingDemandId, Long userId);

  List<MeetingDemandCommentProfile> findAllByMeetingDemandIdAndUserIds(
      Long meetingDemandId, List<Long> userIds);

  void deleteAllByMeetingDemandId(Long meetingDemandId);
}
