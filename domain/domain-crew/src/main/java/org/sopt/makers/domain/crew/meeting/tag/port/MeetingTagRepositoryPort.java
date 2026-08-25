package org.sopt.makers.domain.crew.meeting.tag.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTag;

public interface MeetingTagRepositoryPort {

  MeetingTag save(MeetingTag tag);

  Optional<MeetingTag> findByMeetingId(Long meetingId);

  Optional<MeetingTag> findByFlashId(Long flashId);

  List<MeetingTag> findAllByMeetingIds(List<Long> meetingIds);

  void deleteByMeetingId(Long meetingId);

  void deleteByFlashId(Long flashId);
}
