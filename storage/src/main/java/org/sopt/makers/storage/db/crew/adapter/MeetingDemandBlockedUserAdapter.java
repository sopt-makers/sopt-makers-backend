package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandBlockedUserPort;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandMemberBlockEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandMemberBlockJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingDemandBlockedUserAdapter implements MeetingDemandBlockedUserPort {

  private final MeetingDemandMemberBlockJpaRepository repository;

  @Override
  public Set<Long> findBlockedUserIds(Long blockerUserId, List<Long> candidateUserIds) {
    if (candidateUserIds == null || candidateUserIds.isEmpty()) {
      return Set.of();
    }
    return repository
        .findAllByBlockerIdAndBlockedMemberIdInAndIsBlockedTrue(blockerUserId, candidateUserIds)
        .stream()
        .map(MeetingDemandMemberBlockEntity::getBlockedMemberId)
        .collect(Collectors.toSet());
  }
}
