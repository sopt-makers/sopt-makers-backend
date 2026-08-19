package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.CO_LEADER_CANNOT_APPLY;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public record CoLeaders(List<CoLeader> values) {

  public CoLeaders {
    values = values == null ? List.of() : List.copyOf(values);
  }

  public boolean isCoLeader(Long meetingId, Long userId) {
    return values.stream()
        .anyMatch(
            coLeader -> coLeader.meetingId().equals(meetingId) && coLeader.userId().equals(userId));
  }

  public void validateNotCoLeader(Long meetingId, Long userId) {
    if (isCoLeader(meetingId, userId)) {
      throw new MeetingException(CO_LEADER_CANNOT_APPLY);
    }
  }
}
