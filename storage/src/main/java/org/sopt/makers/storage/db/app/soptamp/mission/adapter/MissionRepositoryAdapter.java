package org.sopt.makers.storage.db.app.soptamp.mission.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.port.MissionRepositoryPort;
import org.sopt.makers.storage.db.app.soptamp.mission.entity.MissionEntity;
import org.sopt.makers.storage.db.app.soptamp.mission.repository.MissionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionRepositoryAdapter implements MissionRepositoryPort {

  private final MissionJpaRepository missionJpaRepository;

  @Override
  public Optional<Mission> findById(Long missionId) {
    return missionJpaRepository.findById(missionId).map(MissionEntity::toDomain);
  }

  @Override
  @Transactional
  public Mission save(Mission mission) {
    return missionJpaRepository.save(MissionEntity.from(mission)).toDomain();
  }

  @Override
  public List<Mission> findAllByDisplay(boolean display) {
    return missionJpaRepository.findAllByDisplay(display).stream()
        .map(MissionEntity::toDomain)
        .toList();
  }

  @Override
  public List<Mission> findAllByDisplayOrderByLevelAscTitleAsc(boolean display) {
    return missionJpaRepository.findAllByDisplayOrderByLevelAscTitleAsc(display).stream()
        .map(MissionEntity::toDomain)
        .toList();
  }

  @Override
  public List<Mission> findByIdsOrderByLevelAndTitle(List<Long> missionIds) {
    if (missionIds.isEmpty()) {
      return List.of();
    }
    return missionJpaRepository.findByIdInOrderByLevelAscTitleAsc(missionIds).stream()
        .map(MissionEntity::toDomain)
        .toList();
  }

  @Override
  public List<Mission> findDisplayedByIdsOrderByLevelAndTitle(List<Long> missionIds) {
    if (missionIds.isEmpty()) {
      return List.of();
    }
    return missionJpaRepository.findDisplayedByIdInOrderByLevelAscTitleAsc(missionIds).stream()
        .map(MissionEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public void deleteAll() {
    missionJpaRepository.deleteAll();
  }
}
