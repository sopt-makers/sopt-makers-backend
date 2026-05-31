package org.sopt.makers.domain.user.service;

import static org.sopt.makers.domain.user.exception.UserFailure.NOT_FOUND_USER;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.sopt.makers.domain.user.exception.UserException;
import org.sopt.makers.domain.user.port.UserCacheRepositoryPort;
import org.sopt.makers.domain.user.port.UserRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

  private final UserRepositoryPort userRepositoryPort;
  private final UserCacheRepositoryPort userCacheRepositoryPort;

  public User getById(Long userId) {
    return userRepositoryPort.findById(userId).orElseThrow(() -> new UserException(NOT_FOUND_USER));
  }

  public User getWithActivitiesById(Long userId) {
    return userRepositoryPort
        .findWithActivitiesById(userId)
        .orElseThrow(() -> new UserException(NOT_FOUND_USER));
  }

  public User getByPhone(String phone) {
    return userRepositoryPort
        .findByPhone(phone)
        .orElseThrow(() -> new UserException(NOT_FOUND_USER));
  }

  /** 소셜 계정 존재 여부를 Optional로 반환한다. 없으면 호출자가 도메인 의미에 맞는 예외를 결정한다. */
  public Optional<User> findBySocialAccount(OAuthPlatform platform, String platformId) {
    return userRepositoryPort.findBySocialAccount(platform, platformId);
  }

  /** 캐시에서 먼저 조회하고, 없는 유저만 DB에서 로드한 뒤 캐시에 적재한다. */
  public List<User> getUsers(List<Long> userIds) {
    Map<Long, User> cached = userCacheRepositoryPort.getAllPresent(userIds);
    loadMissingIntoCache(userIds, cached);
    return userIds.stream().map(cached::get).filter(Objects::nonNull).toList();
  }

  public Page<User> getUsersByCondition(
      UserSearchCondition condition, Pageable pageable, UserSortType sortType) {
    return userRepositoryPort.findPageByCondition(condition, pageable, sortType);
  }

  public boolean existsByPhone(String phone) {
    return userRepositoryPort.existsByPhone(phone);
  }

  public int countByGenerationAndIsSopt(int generation, boolean isSopt) {
    return userRepositoryPort.countByGenerationAndIsSopt(generation, isSopt);
  }

  private void loadMissingIntoCache(List<Long> userIds, Map<Long, User> cached) {
    List<Long> missingIds = userIds.stream().filter(id -> !cached.containsKey(id)).toList();
    if (missingIds.isEmpty()) {
      return;
    }
    userRepositoryPort
        .findAllWithActivitiesByIds(missingIds)
        .forEach(
            user -> {
              userCacheRepositoryPort.put(user);
              cached.put(user.id(), user);
            });
  }
}
