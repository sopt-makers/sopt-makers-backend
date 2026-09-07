package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.domain.user.port.UserLinkRepositoryPort;
import org.sopt.makers.storage.db.user.entity.UserLinkEntity;
import org.sopt.makers.storage.db.user.repository.UserLinkJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLinkRepositoryAdapter implements UserLinkRepositoryPort {

  private final UserLinkJpaRepository userLinkJpaRepository;

  @Transactional
  @Override
  public List<UserLink> replaceAll(final Long userId, final List<UserLink> links) {
    userLinkJpaRepository.deleteAllByUserId(userId);
    List<UserLinkEntity> entities =
        links.stream().map(link -> UserLinkEntity.fromLinkForUser(userId, link)).toList();
    return userLinkJpaRepository.saveAll(entities).stream().map(UserLinkEntity::toDomain).toList();
  }

  @Override
  public Optional<UserLink> findById(final Long linkId) {
    return userLinkJpaRepository.findById(linkId).map(UserLinkEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(final Long linkId) {
    userLinkJpaRepository.deleteById(linkId);
  }

  @Override
  public Map<Long, List<UserLink>> findAllLinksByUserIds(final List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }

    return userLinkJpaRepository.findAllByUserIdIn(userIds).stream()
        .map(UserLinkEntity::toDomain)
        .collect(Collectors.groupingBy(UserLink::userId));
  }

  @Override
  public List<UserLink> findAllByUserId(final Long userId) {
    return userLinkJpaRepository.findAllByUserId(userId).stream().map(UserLinkEntity::toDomain).toList();
  }
}
