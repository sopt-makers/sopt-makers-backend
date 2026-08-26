package org.sopt.makers.storage.db.crew.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandRepositoryPort;
import org.sopt.makers.storage.db.common.PageMapper;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandRepositoryAdapter implements MeetingDemandRepositoryPort {

  private final MeetingDemandJpaRepository repository;

  @Override
  @Transactional
  public MeetingDemand save(MeetingDemand meetingDemand) {
    return repository.save(MeetingDemandEntity.fromDomain(meetingDemand)).toDomain();
  }

  @Override
  public Optional<MeetingDemand> findById(Long meetingDemandId) {
    return repository.findById(meetingDemandId).map(MeetingDemandEntity::toDomain);
  }

  @Override
  public Optional<MeetingDemand> findByIdForUpdate(Long meetingDemandId) {
    return repository.findByIdForUpdate(meetingDemandId).map(MeetingDemandEntity::toDomain);
  }

  @Override
  public PageResult<MeetingDemand> findAll(PageQuery pageQuery) {
    Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
    return PageMapper.toPageResult(
        repository.findAll(PageMapper.toPageable(pageQuery, sort)), MeetingDemandEntity::toDomain);
  }

  @Override
  @Transactional
  public void delete(MeetingDemand meetingDemand) {
    repository.delete(MeetingDemandEntity.fromDomain(meetingDemand));
  }
}
