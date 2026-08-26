package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.sopt.makers.domain.user.port.UserRepositoryPort;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.sopt.makers.storage.db.user.querydsl.UserQuerydslRepository;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.sopt.makers.storage.db.user.repository.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRepositoryAdapter implements UserRepositoryPort {

  private final UserJpaRepository userJpaRepository;
  private final UserActivityHistoryJpaRepository activityJpaRepository;
  private final UserQuerydslRepository userQuerydslRepository;

  @Override
  public Optional<User> findById(Long userId) {
    return userJpaRepository.findById(userId).map(UserEntity::toDomain);
  }

  @Override
  public Optional<User> findWithActivitiesById(Long userId) {
    return userJpaRepository
        .findById(userId)
        .map(
            user -> {
              List<UserActivityHistoryEntity> activities =
                  activityJpaRepository.findByUserId(userId);
              return user.toDomainWithActivities(activities);
            });
  }

  @Override
  public Optional<User> findBySocialAccount(OAuthPlatform platformType, String platformId) {
    return userJpaRepository
        .findByAuthPlatform(platformType, platformId)
        .map(
            user -> {
              List<UserActivityHistoryEntity> activities =
                  activityJpaRepository.findByUserId(user.getId());
              return user.toDomainWithActivities(activities);
            });
  }

  @Override
  public Optional<User> findByPhone(String phone) {
    return userJpaRepository.findByPhone(phone).map(UserEntity::toDomain);
  }

  @Override
  public List<User> findAllWithActivitiesByIds(List<Long> userIds) {
    List<UserEntity> users = userJpaRepository.findAllById(userIds);
    Map<Long, List<UserActivityHistoryEntity>> activitiesByUserId =
        activityJpaRepository.findAllByUserIdIn(userIds).stream()
            .collect(Collectors.groupingBy(a -> a.getUser().getId()));
    return users.stream()
        .map(u -> u.toDomainWithActivities(activitiesByUserId.getOrDefault(u.getId(), List.of())))
        .toList();
  }

  @Override
  public List<Long> findAllUserIds() {
    return userJpaRepository.findAllUserIds();
  }

  @Override
  public boolean existsByPhone(String phone) {
    return userJpaRepository.existsByPhone(phone);
  }

  @Override
  public int countByGenerationAndIsSopt(int generation, boolean isSopt) {
    return activityJpaRepository.countDistinctUserByGenerationAndIsSopt(generation, isSopt);
  }

  @Transactional
  @Override
  public User save(User user) {
    return userJpaRepository.save(UserEntity.fromDomain(user)).toDomain();
  }

  /** QueryDSL로 조건/정렬에 맞는 id 목록을 먼저 조회하고, id IN 절로 활동 이력을 포함한 유저를 한 번에 로드한다. */
  @Override
  public Page<User> findPageByCondition(
      UserSearchCondition condition, Pageable pageable, UserSortType sortType) {
    Page<Long> idPage =
        userQuerydslRepository.findUserIdsByCondition(condition, pageable, sortType);

    List<Long> orderedIds = idPage.getContent();
    if (orderedIds.isEmpty()) {
      return new PageImpl<>(List.of(), pageable, idPage.getTotalElements());
    }

    Map<Long, UserEntity> entityById =
        userJpaRepository.findAllById(orderedIds).stream()
            .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

    Map<Long, List<UserActivityHistoryEntity>> activitiesByUserId =
        activityJpaRepository.findAllByUserIdIn(orderedIds).stream()
            .collect(Collectors.groupingBy(a -> a.getUser().getId()));

    List<User> orderedUsers =
        orderedIds.stream()
            .map(entityById::get)
            .filter(Objects::nonNull)
            .map(
                u ->
                    u.toDomainWithActivities(activitiesByUserId.getOrDefault(u.getId(), List.of())))
            .toList();

    return new PageImpl<>(orderedUsers, pageable, idPage.getTotalElements());
  }
}
