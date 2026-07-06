package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;

public interface MeetingApplyRepositoryPort {

  MeetingApply save(MeetingApply apply);

  Optional<MeetingApply> findById(Long applyId);

  List<MeetingApply> findAllByMeetingId(Long meetingId);

  List<MeetingApply> findAllByMeetingIdAndStatus(Long meetingId, MeetingApplyStatus status);

  List<MeetingApply> findAllByMeetingIdAndStatuses(
      Long meetingId, List<MeetingApplyStatus> statuses);

  List<MeetingApply> findAllByUserIdAndStatus(Long userId, MeetingApplyStatus status);

  boolean existsByMeetingIdAndUserId(Long meetingId, Long userId);

  void deleteByMeetingIdAndUserId(Long meetingId, Long userId);

  void deleteAllByMeetingId(Long meetingId);
}
