package org.sopt.makers.storage.db.user.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.port.UserCareerRepositoryPort;
import org.sopt.makers.storage.db.user.entity.UserCareerEntity;
import org.sopt.makers.storage.db.user.repository.UserCareerJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCareerRepositoryAdapter implements UserCareerRepositoryPort {

  private final UserCareerJpaRepository userCareerJpaRepository;

  @Transactional
  @Override
  public List<UserCareer> replaceAll(final Long userId, final List<UserCareer> careers) {
    userCareerJpaRepository.deleteAllByUserId(userId);
    List<UserCareerEntity> entities =
        careers.stream()
            .map(
                career ->
                    UserCareerEntity.from(
                        UserCareer.of(
                            null,
                            userId,
                            career.companyName(),
                            career.title(),
                            career.startDate(),
                            career.endDate(),
                            career.isCurrent())))
            .toList();
    return userCareerJpaRepository.saveAll(entities).stream()
        .map(UserCareerEntity::toDomain)
        .toList();
  }

  @Override
  public List<UserCareer> findLastCareersByUserIds(final List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }

    List<UserCareerEntity> candidates =
        userCareerJpaRepository.findAllByUserIdInOrderByUserIdAscStartDateDescIdDesc(userIds);

    Map<Long, UserCareer> lastCareerByUserId = new LinkedHashMap<>();
    for (UserCareerEntity candidate : candidates) {
      lastCareerByUserId.putIfAbsent(candidate.getUserId(), candidate.toDomain());
    }

    return List.copyOf(lastCareerByUserId.values());
  }

  @Override
  public Map<Long, List<UserCareer>> findAllCareersByUserIds(final List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }

    List<UserCareerEntity> entities =
        userCareerJpaRepository.findAllByUserIdInOrderByUserIdAscStartDateDescIdDesc(userIds);

    return entities.stream()
        .map(UserCareerEntity::toDomain)
        .collect(Collectors.groupingBy(UserCareer::userId));
  }

  @Override
  public List<UserCareer> findAllByUserId(final Long userId) {
    return userCareerJpaRepository.findAllByUserIdOrderByStartDateDescIdDesc(userId).stream()
        .map(UserCareerEntity::toDomain)
        .toList();
  }
}
