package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandMemberBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDemandMemberBlockJpaRepository
    extends JpaRepository<MeetingDemandMemberBlockEntity, Long> {

  List<MeetingDemandMemberBlockEntity> findAllByBlockerIdAndBlockedMemberIdInAndIsBlockedTrue(
      Long blockerId, List<Long> blockedMemberIds);
}
