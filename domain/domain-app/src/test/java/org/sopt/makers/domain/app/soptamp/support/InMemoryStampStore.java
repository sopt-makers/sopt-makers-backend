package org.sopt.makers.domain.app.soptamp.support;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayRankSource;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.springframework.data.domain.Pageable;

public final class InMemoryStampStore implements StampRepositoryPort {

  private final List<Stamp> stamps = new ArrayList<>();
  private boolean failNextIncrease;
  private int interleavedIncrement;

  public void failNextIncrease() {
    this.failNextIncrease = true;
  }

  public void interleaveIncreaseBefore(int increment) {
    this.interleavedIncrement = increment;
  }

  @Override
  public Stamp save(Stamp stamp) {
    stamps.removeIf(it -> it.id().equals(stamp.id()));
    stamps.add(stamp);
    return stamp;
  }

  @Override
  public Optional<Stamp> findById(Long stampId) {
    return stamps.stream().filter(it -> it.id().equals(stampId)).findFirst();
  }

  @Override
  public int increaseClapCount(Long stampId, int increment) {
    if (failNextIncrease) {
      failNextIncrease = false;
      return 0;
    }
    if (interleavedIncrement > 0) {
      int interleaved = interleavedIncrement;
      interleavedIncrement = 0;
      increaseClapCount(stampId, interleaved);
    }
    Optional<Stamp> found = findById(stampId);
    if (found.isEmpty()) {
      return 0;
    }
    Stamp stamp = found.get();
    save(
        new Stamp(
            stamp.id(),
            stamp.contents(),
            stamp.images(),
            stamp.userId(),
            stamp.missionId(),
            stamp.activityDate(),
            stamp.createdAt(),
            stamp.updatedAt(),
            stamp.clapCount() + increment,
            stamp.viewCount(),
            stamp.version()));
    return 1;
  }

  @Override
  public Optional<Stamp> findByIdAndUserId(Long stampId, Long userId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Stamp> findByUserIdAndMissionId(Long userId, Long missionId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Stamp> findAll() {
    return List.copyOf(stamps);
  }

  @Override
  public List<Stamp> findAllByUserId(Long userId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Stamp> findAllByUserIdIn(Collection<Long> userIds) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Stamp> findDisplayedLatestStamps(Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AppjamTodayRankSource> findTodayUserRankSources(
      LocalDateTime todayStart, LocalDateTime tomorrowStart) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(Long stampId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteAllByUserId(Long userId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteAll() {
    stamps.clear();
  }

  @Override
  public boolean existsByUserIdInAndMissionId(Collection<Long> userIds, Long missionId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Stamp updateContents(
      Long stampId, String contents, List<String> images, String activityDate) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void increaseViewCount(Long stampId) {
    throw new UnsupportedOperationException();
  }
}
