package org.sopt.makers.storage.db.app.soptamp.stamp.adapter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayRankSource;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.StampCounts;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class StampRepositoryStubAdapter implements StampRepositoryPort {

  private static UnsupportedOperationException notMigrated() {
    return new UnsupportedOperationException("stamp 저장소가 아직 이관되지 않았습니다.");
  }

  @Override
  public Optional<Stamp> findById(Long stampId) {
    throw notMigrated();
  }

  @Override
  public Optional<Stamp> findByIdAndUserId(Long stampId, Long userId) {
    throw notMigrated();
  }

  @Override
  public Optional<Stamp> findByUserIdAndMissionId(Long userId, Long missionId) {
    throw notMigrated();
  }

  @Override
  public List<Stamp> findAll() {
    throw notMigrated();
  }

  @Override
  public List<Stamp> findAllByUserId(Long userId) {
    throw notMigrated();
  }

  @Override
  public List<Stamp> findAllByUserIdIn(Collection<Long> userIds) {
    throw notMigrated();
  }

  @Override
  public List<Stamp> findDisplayedLatestStamps(Pageable pageable) {
    throw notMigrated();
  }

  @Override
  public List<AppjamTodayRankSource> findTodayUserRankSources(
      LocalDateTime todayStart, LocalDateTime tomorrowStart) {
    throw notMigrated();
  }

  @Override
  public Stamp save(Stamp stamp) {
    throw notMigrated();
  }

  @Override
  public void deleteById(Long stampId) {
    throw notMigrated();
  }

  @Override
  public void deleteAllByUserId(Long userId) {
    throw notMigrated();
  }

  @Override
  public void deleteAll() {
    throw notMigrated();
  }

  @Override
  public boolean existsByUserIdInAndMissionId(Collection<Long> userIds, Long missionId) {
    throw notMigrated();
  }

  @Override
  public StampCounts incrementClapCountReturning(Long stampId, int increment) {
    throw notMigrated();
  }

  @Override
  public void increaseViewCount(Long stampId) {
    throw notMigrated();
  }
}
