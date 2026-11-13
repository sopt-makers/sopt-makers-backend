package org.sopt.makers.domain.crew.dining.event;

import java.util.List;

/** 회차 삭제 트랜잭션에서 확정한 수신자, 회식명과 삭제 당시 회차 번호. */
public record DiningRoundDeletedEvent(
    Long meetingId, String diningTitle, int roundNumber, List<Long> receiverUserIds) {
  public DiningRoundDeletedEvent {
    receiverUserIds = List.copyOf(receiverUserIds);
  }
}
