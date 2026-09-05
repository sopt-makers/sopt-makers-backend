package org.sopt.makers.storage.db.playground.member.relation.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.relation.UserBlock;
import org.sopt.makers.domain.playground.member.relation.port.UserBlockRepositoryPort;
import org.sopt.makers.storage.db.playground.member.relation.entity.UserBlockEntity;
import org.sopt.makers.storage.db.playground.member.relation.repository.UserBlockJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBlockRepositoryAdapter implements UserBlockRepositoryPort {

  private final UserBlockJpaRepository userBlockJpaRepository;

  @Transactional
  @Override
  public UserBlock save(UserBlock userBlock) {
    return userBlockJpaRepository.save(UserBlockEntity.fromDomain(userBlock)).toDomain();
  }

  @Override
  public Optional<UserBlock> findById(Long id) {
    return userBlockJpaRepository.findById(id).map(UserBlockEntity::toDomain);
  }

  @Override
  public Optional<UserBlock> findByBlockerUserIdAndBlockedUserId(
      Long blockerUserId, Long blockedUserId) {
    return userBlockJpaRepository
        .findByBlockerUserIdAndBlockedUserId(blockerUserId, blockedUserId)
        .map(UserBlockEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long id) {
    userBlockJpaRepository.deleteById(id);
  }
}
