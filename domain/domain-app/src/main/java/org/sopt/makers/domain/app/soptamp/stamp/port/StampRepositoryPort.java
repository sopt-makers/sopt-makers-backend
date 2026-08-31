package org.sopt.makers.domain.app.soptamp.stamp.port;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayRankSource;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.StampCounts;
import org.springframework.data.domain.Pageable;

public interface StampRepositoryPort {

  Optional<Stamp> findById(Long stampId);

  Optional<Stamp> findByIdAndUserId(Long stampId, Long userId);

  Optional<Stamp> findByUserIdAndMissionId(Long userId, Long missionId);

  List<Stamp> findAll();

  List<Stamp> findAllByUserId(Long userId);

  List<Stamp> findAllByUserIdIn(Collection<Long> userIds);

  List<Stamp> findDisplayedLatestStamps(Pageable pageable);

  List<AppjamTodayRankSource> findTodayUserRankSources(
      LocalDateTime todayStart, LocalDateTime tomorrowStart);

  Stamp save(Stamp stamp);

  void deleteById(Long stampId);

  void deleteAllByUserId(Long userId);

  void deleteAll();

  boolean existsByUserIdInAndMissionId(Collection<Long> userIds, Long missionId);

  StampCounts incrementClapCountReturning(Long stampId, int increment);

  void increaseViewCount(Long stampId);
}
