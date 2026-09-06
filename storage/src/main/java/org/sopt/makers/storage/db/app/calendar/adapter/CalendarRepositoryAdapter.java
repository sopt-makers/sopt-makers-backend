package org.sopt.makers.storage.db.app.calendar.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.port.CalendarRepositoryPort;
import org.sopt.makers.storage.db.app.calendar.entity.CalendarEntity;
import org.sopt.makers.storage.db.app.calendar.repository.CalendarJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarRepositoryAdapter implements CalendarRepositoryPort {

  private final CalendarJpaRepository calendarJpaRepository;

  @Override
  public List<Calendar> findAllByGenerationOrderByStartDateAscEndDateAsc(int generation) {
    return calendarJpaRepository
        .findAllByGenerationOrderByStartDateAscEndDateAsc(generation)
        .stream()
        .map(CalendarEntity::toDomain)
        .toList();
  }
}
