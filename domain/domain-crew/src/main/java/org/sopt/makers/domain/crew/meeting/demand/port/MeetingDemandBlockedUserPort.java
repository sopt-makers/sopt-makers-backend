package org.sopt.makers.domain.crew.meeting.demand.port;

import java.util.List;
import java.util.Set;

public interface MeetingDemandBlockedUserPort {

  Set<Long> findBlockedUserIds(Long blockerUserId, List<Long> candidateUserIds);
}
