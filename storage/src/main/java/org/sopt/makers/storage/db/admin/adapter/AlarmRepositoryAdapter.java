package org.sopt.makers.storage.db.admin.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmStatus;
import org.sopt.makers.domain.admin.alarm.exception.AlarmException;
import org.sopt.makers.domain.admin.alarm.exception.AlarmFailure;
import org.sopt.makers.domain.admin.alarm.port.AlarmRepositoryPort;
import org.sopt.makers.storage.db.admin.entity.AlarmEntity;
import org.sopt.makers.storage.db.admin.repository.AlarmJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmRepositoryAdapter implements AlarmRepositoryPort {

  private final AlarmJpaRepository alarmJpaRepository;

  @Override
  @Transactional
  public Alarm save(Alarm alarm) {
    AlarmEntity entity =
        alarm.id() != null
            ? alarmJpaRepository
                .findById(alarm.id())
                .map(
                    e -> {
                      e.updateStatus(alarm.status(), alarm.sendAt());
                      return e;
                    })
                .orElseThrow(() -> new AlarmException(AlarmFailure.NOT_FOUND_ALARM))
            : AlarmEntity.from(alarm);
    return alarmJpaRepository.save(entity).toDomain();
  }

  @Override
  public Optional<Alarm> findById(long id) {
    return alarmJpaRepository.findById(id).map(AlarmEntity::toDomain);
  }

  @Override
  public List<Alarm> findAllOrdered(Integer generation, AlarmStatus status, int page, int size) {
    return alarmJpaRepository.findOrderByCreatedAt(generation, status, page, size).stream()
        .map(AlarmEntity::toDomain)
        .toList();
  }

  @Override
  public int count(Integer generation, AlarmStatus status) {
    return alarmJpaRepository.count(generation, status);
  }

  @Override
  @Transactional
  public void delete(Alarm alarm) {
    alarmJpaRepository.findById(alarm.id()).ifPresent(alarmJpaRepository::delete);
  }
}
