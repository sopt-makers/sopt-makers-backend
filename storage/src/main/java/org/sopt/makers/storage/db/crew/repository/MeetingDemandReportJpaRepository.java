package org.sopt.makers.storage.db.crew.repository;

import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReportTarget;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDemandReportJpaRepository
    extends JpaRepository<MeetingDemandReportEntity, Long> {

  boolean existsByUserIdAndTargetTypeAndTargetId(
      Long userId, MeetingDemandReportTarget targetType, Long targetId);

  void deleteAllByTargetTypeAndTargetId(MeetingDemandReportTarget targetType, Long targetId);
}
