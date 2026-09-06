package org.sopt.makers.domain.playground.post.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.post.MeetingPostContext;

public interface MeetingPostAccessPort {

  Optional<MeetingPostContext> findMeeting(Long meetingId, Long userId);

  List<MeetingPostContext> findMeetingsByUserId(Long userId);

  List<Long> findMemberIds(Long meetingId);
}
