package org.sopt.makers.storage.db.app.soptamp.stamp.adapter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayRankSource;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.sopt.makers.storage.db.app.soptamp.stamp.entity.StampEntity;
import org.sopt.makers.storage.db.app.soptamp.stamp.repository.StampJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StampRepositoryAdapter implements StampRepositoryPort {

  private final StampJpaRepository stampJpaRepository;

  @Override
  public Optional<Stamp> findById(Long stampId) {
    return stampJpaRepository.findById(stampId).map(StampEntity::toDomain);
  }

  @Override
  public Optional<Stamp> findByIdAndUserId(Long stampId, Long userId) {
    return stampJpaRepository.findByIdAndUserId(stampId, userId).map(StampEntity::toDomain);
  }

  @Override
  public Optional<Stamp> findByUserIdAndMissionId(Long userId, Long missionId) {
    return stampJpaRepository
        .findByUserIdAndMissionId(userId, missionId)
        .map(StampEntity::toDomain);
  }

  @Override
  public List<Stamp> findAll() {
    return stampJpaRepository.findAll().stream().map(StampEntity::toDomain).toList();
  }

  @Override
  public List<Stamp> findAllByUserId(Long userId) {
    return stampJpaRepository.findAllByUserId(userId).stream().map(StampEntity::toDomain).toList();
  }

  @Override
  public List<Stamp> findAllByUserIdIn(Collection<Long> userIds) {
    return stampJpaRepository.findAllByUserIdIn(userIds).stream()
        .map(StampEntity::toDomain)
        .toList();
  }

  @Override
  public List<Stamp> findDisplayedLatestStamps(Pageable pageable) {
    return stampJpaRepository.findAllByMissionDisplayTrueOrderByCreatedAtDesc(pageable).stream()
        .map(StampEntity::toDomain)
        .toList();
  }

  @Override
  public List<AppjamTodayRankSource> findTodayUserRankSources(
      LocalDateTime todayStart, LocalDateTime tomorrowStart) {
    return stampJpaRepository.findTodayUserRankSources(todayStart, tomorrowStart).stream()
        .map(
            row ->
                new AppjamTodayRankSource(
                    row.getUserId(), row.getTodayPoints(), row.getFirstCertifiedAtToday()))
        .toList();
  }

  @Override
  @Transactional
  public Stamp save(Stamp stamp) {
    return stampJpaRepository.save(StampEntity.from(stamp)).toDomain();
  }

  @Override
  @Transactional
  public Stamp updateContents(
      Long stampId, String contents, List<String> images, String activityDate) {
    StampEntity entity =
        stampJpaRepository
            .findById(stampId)
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_STAMP));
    entity.applyEdit(contents, images, activityDate);
    return entity.toDomain();
  }

  @Override
  @Transactional
  public void deleteById(Long stampId) {
    stampJpaRepository.deleteById(stampId);
  }

  @Override
  @Transactional
  public void deleteAllByUserId(Long userId) {
    stampJpaRepository.deleteAllByUserId(userId);
  }

  @Override
  @Transactional
  public void deleteAll() {
    stampJpaRepository.deleteAll();
  }

  @Override
  public boolean existsByUserIdInAndMissionId(Collection<Long> userIds, Long missionId) {
    return stampJpaRepository.existsByUserIdInAndMissionId(userIds, missionId);
  }

  @Override
  @Transactional
  public void increaseViewCount(Long stampId) {
    stampJpaRepository.increaseViewCount(stampId);
  }
}
