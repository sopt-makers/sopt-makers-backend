package org.sopt.makers.storage.db.app.soptamp.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.port.SoptampProfileSourcePort;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.sopt.makers.storage.db.user.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptampProfileSourceAdapter implements SoptampProfileSourcePort {

  private final UserJpaRepository userJpaRepository;
  private final UserActivityHistoryJpaRepository userActivityHistoryJpaRepository;

  @Override
  public List<User> findAllForUpsert() {
    Map<Long, List<UserActivityHistoryEntity>> activitiesByUserId =
        groupByUserId(userActivityHistoryJpaRepository.findAll());
    return userJpaRepository.findAllById(activitiesByUserId.keySet()).stream()
        .map(user -> user.toDomainWithActivities(activitiesByUserId.get(user.getId())))
        .toList();
  }

  @Override
  public List<User> findAllByUserIds(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return List.of();
    }
    Map<Long, List<UserActivityHistoryEntity>> activitiesByUserId =
        groupByUserId(userActivityHistoryJpaRepository.findAllByUserIdIn(userIds));
    return userJpaRepository.findAllById(userIds).stream()
        .map(
            user ->
                user.toDomainWithActivities(
                    activitiesByUserId.getOrDefault(user.getId(), List.of())))
        .toList();
  }

  @Override
  public Optional<User> findByUserId(Long userId) {
    return userJpaRepository
        .findById(userId)
        .map(
            user ->
                user.toDomainWithActivities(userActivityHistoryJpaRepository.findByUserId(userId)));
  }

  private Map<Long, List<UserActivityHistoryEntity>> groupByUserId(
      List<UserActivityHistoryEntity> activities) {
    return activities.stream().collect(Collectors.groupingBy(a -> a.getUser().getId()));
  }
}
