package org.sopt.makers.storage.db.crew.adapter;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.*;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.*;
import org.sopt.makers.domain.crew.dining.model.enums.*;
import org.sopt.makers.domain.crew.dining.model.projection.*;
import org.sopt.makers.domain.crew.dining.port.*;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.storage.db.crew.entity.*;
import org.sopt.makers.storage.db.crew.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiningRoundRepositoryAdapter implements DiningRoundPort {
  private final DiningRoundJpaRepository repository;

  @Override
  public List<DiningRound> findAllByMeetingIdOrderByIdAsc(Long meetingId) {
    return repository.findAllByMeetingIdOrderByIdAsc(meetingId).stream()
        .map(DiningRoundEntity::toDomain)
        .toList();
  }

  @Override
  public List<DiningRound> findAllByMeetingIdInOrderByIdAsc(List<Long> meetingIds) {
    return repository.findAllByMeetingIdInOrderByIdAsc(meetingIds).stream()
        .map(DiningRoundEntity::toDomain)
        .toList();
  }

  @Override
  public List<DiningRound> findAllByMeetingIdForUpdate(Long meetingId) {
    return repository.findAllByMeetingIdForUpdate(meetingId).stream()
        .map(DiningRoundEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public int closeOpenRoundsOfDiningsBefore(
      DiningRoundStatus closed,
      DiningRoundStatus open,
      MeetingCategory category,
      LocalDateTime today,
      LocalDateTime now) {
    repository.lockExpiredDinings(today);
    return repository.closeOpenRoundsOfDiningsBefore(closed, open, category, today, now);
  }

  @Transactional
  @Override
  public DiningRound save(DiningRound value) {
    if (value.getId() != null) {
      var entity =
          repository
              .findById(value.getId())
              .orElseThrow(() -> new DiningException(DiningFailure.NOT_FOUND_DINING_ROUND));
      entity.updateFromDomain(value);
      return entity.toDomain();
    }
    return repository.save(DiningRoundEntity.fromDomain(value)).toDomain();
  }

  @Transactional
  @Override
  public void delete(DiningRound value) {
    repository.delete(DiningRoundEntity.fromDomain(value));
  }

  @Transactional
  @Override
  public void deleteAllInBatch(List<DiningRound> values) {
    repository.deleteAllInBatch(values.stream().map(DiningRoundEntity::fromDomain).toList());
  }

  @Override
  public Optional<DiningRound> findById(Long id) {
    return repository.findById(id).map(DiningRoundEntity::toDomain);
  }
}
