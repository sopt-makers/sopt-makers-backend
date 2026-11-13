package org.sopt.makers.storage.db.crew.adapter;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.sopt.makers.core.pagination.*;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.*;
import org.sopt.makers.domain.crew.dining.model.enums.*;
import org.sopt.makers.domain.crew.dining.model.projection.*;
import org.sopt.makers.domain.crew.dining.port.*;
import org.sopt.makers.storage.db.common.PageMapper;
import org.sopt.makers.storage.db.crew.entity.*;
import org.sopt.makers.storage.db.crew.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiningAttendanceRepositoryAdapter implements DiningAttendancePort {
  private final DiningAttendanceJpaRepository repository;

  @Override
  public boolean existsByRoundIdAndUserId(Long roundId, Long userId) {
    return repository.existsByRoundIdAndUserId(roundId, userId);
  }

  @Override
  public Optional<DiningAttendance> findByRoundIdAndUserId(Long roundId, Long userId) {
    return repository.findByRoundIdAndUserId(roundId, userId).map(DiningAttendanceEntity::toDomain);
  }

  @Override
  public Optional<DiningAttendance> findByRoundIdAndUserIdForUpdate(Long roundId, Long userId) {
    return repository
        .findByRoundIdAndUserIdForUpdate(roundId, userId)
        .map(DiningAttendanceEntity::toDomain);
  }

  @Override
  public List<DiningAttendance> findAllByRoundIdInForUpdate(List<Long> roundIds) {
    return repository.findAllByRoundIdInForUpdate(roundIds).stream()
        .map(DiningAttendanceEntity::toDomain)
        .toList();
  }

  @Override
  public List<DiningAttendance> findAllByRoundIdInAndPayerStatusOrderByPayerRequestedAtAscIdAsc(
      List<Long> roundIds, DiningPayerStatus payerStatus) {
    return repository
        .findAllByRoundIdInAndPayerStatusOrderByPayerRequestedAtAscIdAsc(roundIds, payerStatus)
        .stream()
        .map(DiningAttendanceEntity::toDomain)
        .toList();
  }

  @Override
  public List<DiningAttendance> findAllByRoundIdIn(List<Long> roundIds) {
    return repository.findAllByRoundIdIn(roundIds).stream()
        .map(DiningAttendanceEntity::toDomain)
        .toList();
  }

  @Override
  public List<DiningAttendance> findAllByRoundId(Long roundId) {
    return repository.findAllByRoundId(roundId).stream()
        .map(DiningAttendanceEntity::toDomain)
        .toList();
  }

  @Override
  public List<DiningAttendance> findAllByRoundIdOrderByCreatedTimestampAscIdAsc(Long roundId) {
    return repository.findAllByRoundIdOrderByCreatedAtAscIdAsc(roundId).stream()
        .map(DiningAttendanceEntity::toDomain)
        .toList();
  }

  @Override
  public PageResult<DiningAttendance> findAllByRoundId(
      Long roundId, PageQuery page, boolean ascending) {
    return PageMapper.toPageResult(
        repository.findAllByRoundId(
            roundId,
            PageRequest.of(
                page.page() - 1,
                page.limit(),
                Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt", "id"))),
        DiningAttendanceEntity::toDomain);
  }

  @Override
  public List<Long> findDistinctUserIdsByRoundIdIn(List<Long> roundIds) {
    return repository.findDistinctUserIdsByRoundIdIn(roundIds);
  }

  @Override
  public long countByRoundId(Long roundId) {
    return repository.countByRoundId(roundId);
  }

  @Override
  public boolean existsByRoundIdAndUserIdAndPayerStatus(
      Long roundId, Long userId, DiningPayerStatus payerStatus) {
    return repository.existsByRoundIdAndUserIdAndPayerStatus(roundId, userId, payerStatus);
  }

  @Override
  public List<Long> findMeetingIdsByUserIdAndPayerStatus(
      Long userId, DiningPayerStatus payerStatus, List<Long> meetingIds) {
    return repository.findMeetingIdsByUserIdAndPayerStatus(userId, payerStatus, meetingIds);
  }

  @Override
  public long countDistinctUserIdsByMeetingId(Long meetingId) {
    return repository.countDistinctUserIdsByMeetingId(meetingId);
  }

  @Override
  public List<DiningAttendeeCountProjection> countDistinctUserIdsByMeetingIdIn(
      List<Long> meetingIds) {
    return repository.countDistinctUserIdsByMeetingIdIn(meetingIds);
  }

  @Transactional
  @Override
  public DiningAttendance save(DiningAttendance value) {
    if (value.getId() != null) {
      var entity =
          repository
              .findById(value.getId())
              .orElseThrow(() -> new DiningException(DiningFailure.NOT_FOUND_DINING_ATTENDANCE));
      entity.updateFromDomain(value);
      return entity.toDomain();
    }
    return repository.save(DiningAttendanceEntity.fromDomain(value)).toDomain();
  }

  @Transactional
  @Override
  public void delete(DiningAttendance value) {
    repository.delete(DiningAttendanceEntity.fromDomain(value));
  }

  @Transactional
  @Override
  public void deleteAllInBatch(List<DiningAttendance> values) {
    repository.deleteAllInBatch(values.stream().map(DiningAttendanceEntity::fromDomain).toList());
  }

  @Transactional
  @Override
  public DiningAttendance saveAndFlush(DiningAttendance value) {
    try {
      return repository.saveAndFlush(DiningAttendanceEntity.fromDomain(value)).toDomain();
    } catch (DataIntegrityViolationException exception) {
      for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
        if (cause instanceof ConstraintViolationException violation) {
          if ("uk_dining_attendance_round_user".equals(violation.getConstraintName()))
            throw new DiningException(DiningFailure.ALREADY_ATTENDED_DINING_ROUND);
          if ("fk_dining_attendance_round".equals(violation.getConstraintName()))
            throw new DiningException(DiningFailure.NOT_FOUND_DINING_ROUND);
        }
      }
      throw exception;
    }
  }
}
