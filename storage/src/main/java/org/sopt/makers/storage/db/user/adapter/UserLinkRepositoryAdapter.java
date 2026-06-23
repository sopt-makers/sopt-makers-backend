package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
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
        links.stream()
            .map(link -> UserLinkEntity.from(UserLink.of(null, userId, link.title(), link.url())))
            .toList();
    return userLinkJpaRepository.saveAll(entities).stream().map(UserLinkEntity::toDomain).toList();
  }
}
