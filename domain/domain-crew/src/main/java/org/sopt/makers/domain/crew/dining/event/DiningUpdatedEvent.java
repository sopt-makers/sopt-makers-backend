package org.sopt.makers.domain.crew.dining.event;

import java.util.List;

/** 회식 수정 트랜잭션에서 확정한 수신자와 회식명. */
public record DiningUpdatedEvent(Long meetingId, String diningTitle, List<Long> receiverUserIds) {
  public DiningUpdatedEvent {
    receiverUserIds = List.copyOf(receiverUserIds);
  }
}
