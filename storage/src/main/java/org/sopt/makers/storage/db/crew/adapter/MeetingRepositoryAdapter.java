package org.sopt.makers.storage.db.crew.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingSearchCondition;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.storage.db.common.PageMapper;
import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

  @Transactional
  @Override
  public Optional<Meeting> findByIdForUpdate(Long meetingId) {
    return meetingJpaRepository.findByIdForUpdate(meetingId).map(MeetingEntity::toDomain);
  }

  @Override
  public List<Meeting> findAllByIds(List<Long> meetingIds) {
    if (meetingIds == null || meetingIds.isEmpty()) {
      return List.of();
    }
    return meetingJpaRepository.findAllById(meetingIds).stream()
        .map(MeetingEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<Long> findFirstIdByTitle(String title) {
    return meetingJpaRepository.findFirstByTitleOrderByIdDesc(title).map(MeetingEntity::getId);
  }

  @Override
  public Optional<Long> findFirstIdByTitleContaining(String title) {
    return meetingJpaRepository
        .findFirstByTitleContainingOrderByIdDesc(title)
        .map(MeetingEntity::getId);
  }

  @Override
  public PageResult<Meeting> findAll(PageQuery pageQuery) {
    return PageMapper.toPageResult(
        meetingJpaRepository.findAll(PageMapper.toPageable(pageQuery)), MeetingEntity::toDomain);
  }

  @Override
  public PageResult<Meeting> search(MeetingSearchCondition condition, PageQuery pageQuery) {
    Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
    return PageMapper.toPageResult(
        meetingJpaRepository.findAll(
            toSpecification(condition), PageMapper.toPageable(pageQuery, sort)),
        MeetingEntity::toDomain);
  }

  @Override
  public PageResult<Meeting> findAllByLeaderUserId(Long userId, PageQuery pageQuery) {
    return PageMapper.toPageResult(
        meetingJpaRepository.findAllByLeaderUserId(userId, PageMapper.toPageable(pageQuery)),
        MeetingEntity::toDomain);
  }

  @Override
  public PageResult<Meeting> findAllByMemberUserId(Long userId, PageQuery pageQuery) {
    Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
    return PageMapper.toPageResult(
        meetingJpaRepository.findAllByMemberUserId(userId, PageMapper.toPageable(pageQuery, sort)),
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

  private Specification<MeetingEntity> toSpecification(MeetingSearchCondition condition) {
    Specification<MeetingEntity> specification =
        (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    if (condition.search() != null) {
      String keyword = "%" + condition.search().toLowerCase(Locale.ROOT) + "%";
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.or(
                      criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keyword),
                      criteriaBuilder.like(criteriaBuilder.lower(root.get("subTitle")), keyword),
                      criteriaBuilder.like(
                          criteriaBuilder.lower(root.get("description")), keyword)));
    }
    if (condition.category() != null) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("category"), condition.category()));
    }
    if (condition.status() != null) {
      specification = specification.and(statusSpecification(condition.status(), condition.now()));
    }
    return specification;
  }

  private Specification<MeetingEntity> statusSpecification(
      MeetingStatus status, LocalDateTime now) {
    return switch (status) {
      case BEFORE_START ->
          (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("startDate"), now);
      case APPLY_ABLE ->
          (root, query, criteriaBuilder) ->
              criteriaBuilder.and(
                  criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), now),
                  criteriaBuilder.greaterThan(root.get("endDate"), now));
      case RECRUITMENT_COMPLETE ->
          (root, query, criteriaBuilder) ->
              criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), now);
    };
  }
}
