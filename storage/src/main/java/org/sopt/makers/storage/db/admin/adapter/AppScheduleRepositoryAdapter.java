package org.sopt.makers.storage.db.admin.adapter;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.app.AppSchedule;
import org.sopt.makers.domain.admin.app.port.AppScheduleRepositoryPort;
import org.sopt.makers.storage.db.admin.entity.ScheduleEntity;
import org.sopt.makers.storage.db.admin.repository.ScheduleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppScheduleRepositoryAdapter implements AppScheduleRepositoryPort {

  private final ScheduleJpaRepository scheduleJpaRepository;

  @Override
  public List<AppSchedule> findBetween(LocalDateTime startAt, LocalDateTime endAt) {
    return scheduleJpaRepository.findBetween(startAt, endAt).stream()
        .map(ScheduleEntity::toDomain)
        .toList();
  }
}
