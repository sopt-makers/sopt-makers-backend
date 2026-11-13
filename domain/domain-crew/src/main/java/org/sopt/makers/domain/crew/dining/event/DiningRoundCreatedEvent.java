package org.sopt.makers.domain.crew.dining.event;

import java.util.List;

public record DiningRoundCreatedEvent(Long meetingId, Long roundId, List<Long> attendeeUserIds) {
  public DiningRoundCreatedEvent {
    attendeeUserIds = List.copyOf(attendeeUserIds);
  }
}
