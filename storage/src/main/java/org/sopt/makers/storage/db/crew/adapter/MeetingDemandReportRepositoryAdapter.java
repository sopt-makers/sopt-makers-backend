package org.sopt.makers.storage.db.crew.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReport;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReportTarget;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandReportRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandReportEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandReportJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandReportRepositoryAdapter implements MeetingDemandReportRepositoryPort {

  private final MeetingDemandReportJpaRepository repository;

  @Override
  @Transactional
  public MeetingDemandReport save(MeetingDemandReport report) {
    return repository.save(MeetingDemandReportEntity.fromDomain(report)).toDomain();
  }

  @Override
  public boolean existsByUserIdAndTarget(
      Long userId, MeetingDemandReportTarget targetType, Long targetId) {
    return repository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
  }

  @Override
  @Transactional
  public void deleteAllByTarget(MeetingDemandReportTarget targetType, Long targetId) {
    repository.deleteAllByTargetTypeAndTargetId(targetType, targetId);
  }
}
