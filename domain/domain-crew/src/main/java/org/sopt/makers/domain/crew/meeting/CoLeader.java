package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.LEADER_CANNOT_BE_CO_LEADER;

import java.time.LocalDateTime;
import java.util.Objects;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public record CoLeader(
    Long id, Long meetingId, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static CoLeader create(Meeting meeting, Long userId) {
    if (Objects.equals(meeting.userId(), userId)) {
      throw new MeetingException(LEADER_CANNOT_BE_CO_LEADER);
    }
    return new CoLeader(null, meeting.id(), userId, null, null);
  }
}
