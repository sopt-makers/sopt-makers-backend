package org.sopt.makers.domain.crew.dining.event;

import java.util.List;

/** 회식 삭제 트랜잭션에서 확정한 수신자와 회식명. 커밋 후에는 회식이 없으므로 발송에 필요한 값을 모두 담는다. */
public record DiningDeletedEvent(Long meetingId, String diningTitle, List<Long> receiverUserIds) {
  public DiningDeletedEvent {
    receiverUserIds = List.copyOf(receiverUserIds);
  }
}
