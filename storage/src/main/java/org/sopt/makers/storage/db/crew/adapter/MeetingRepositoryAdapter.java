package org.sopt.makers.storage.db.crew.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.storage.db.common.PageMapper;
import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingRepositoryAdapter implements MeetingRepositoryPort {

  private final MeetingJpaRepository meetingJpaRepository;

  @Transactional
  @Override
  public Meeting save(Meeting meeting) {
    return meetingJpaRepository.save(MeetingEntity.fromDomain(meeting)).toDomain();
  }

  @Override
  public Optional<Meeting> findById(Long meetingId) {
    return meetingJpaRepository.findById(meetingId).map(MeetingEntity::toDomain);
  }

  @Override
  public PageResult<Meeting> findAll(PageQuery pageQuery) {
    return PageMapper.toPageResult(
        meetingJpaRepository.findAll(PageMapper.toPageable(pageQuery)), MeetingEntity::toDomain);
  }

  @Override
  public PageResult<Meeting> findAllByUserId(Long userId, PageQuery pageQuery) {
    return PageMapper.toPageResult(
        meetingJpaRepository.findAllByUserId(userId, PageMapper.toPageable(pageQuery)),
        MeetingEntity::toDomain);
  }

  @Override
  public PageResult<Meeting> findAllByMeetingDemandId(Long meetingDemandId, PageQuery pageQuery) {
    Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
    return PageMapper.toPageResult(
        meetingJpaRepository.findAllByMeetingDemandId(
            meetingDemandId, PageMapper.toPageable(pageQuery, sort)),
        MeetingEntity::toDomain);
  }

  @Override
  public long countByMeetingDemandId(Long meetingDemandId) {
    return meetingJpaRepository.countByMeetingDemandId(meetingDemandId);
  }

  @Transactional
  @Override
  public void clearMeetingDemandId(Long meetingDemandId) {
    meetingJpaRepository.clearMeetingDemandId(meetingDemandId);
  }

  @Transactional
  @Override
  public void delete(Meeting meeting) {
    meetingJpaRepository.delete(MeetingEntity.fromDomain(meeting));
  }
}
