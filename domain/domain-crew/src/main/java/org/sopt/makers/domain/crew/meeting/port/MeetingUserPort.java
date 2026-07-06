package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.MeetingUser;

public interface MeetingUserPort {

  Optional<MeetingUser> findById(Long userId);

  List<MeetingUser> findAllById(List<Long> userIds);
}
