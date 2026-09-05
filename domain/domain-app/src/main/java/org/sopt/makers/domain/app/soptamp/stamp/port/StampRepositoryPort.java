package org.sopt.makers.domain.app.soptamp.stamp.port;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayRankSource;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
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

  Stamp updateContents(Long stampId, String contents, List<String> images, String activityDate);

  void deleteById(Long stampId);

  void deleteAllByUserId(Long userId);

  void deleteAll();

  boolean existsByUserIdInAndMissionId(Collection<Long> userIds, Long missionId);

  void increaseViewCount(Long stampId);
}
