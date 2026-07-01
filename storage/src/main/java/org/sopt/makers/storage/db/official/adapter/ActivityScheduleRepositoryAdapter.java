package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.activityschedule.ActivitySchedule;
import org.sopt.makers.domain.official.activityschedule.port.ActivityScheduleRepositoryPort;
import org.sopt.makers.storage.db.official.entity.ActivityScheduleEntity;
import org.sopt.makers.storage.db.official.repository.ActivityScheduleJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityScheduleRepositoryAdapter implements ActivityScheduleRepositoryPort {

  private final ActivityScheduleJpaRepository activityScheduleJpaRepository;

  @Transactional
  @Override
  public List<ActivitySchedule> saveAll(List<ActivitySchedule> activitySchedules) {
    List<ActivityScheduleEntity> entities =
        activitySchedules.stream().map(ActivityScheduleEntity::fromDomain).toList();
    return activityScheduleJpaRepository.saveAll(entities).stream()
        .map(ActivityScheduleEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public void deleteByGenerationId(Integer generationId) {
    activityScheduleJpaRepository.deleteByGenerationId(generationId);
  }

  @Override
  public List<ActivitySchedule> findByGenerationIdOrderByStartDateAsc(Integer generationId) {
    return activityScheduleJpaRepository
        .findByGenerationIdOrderByStartDateAsc(generationId)
        .stream()
        .map(ActivityScheduleEntity::toDomain)
        .toList();
  }
}
