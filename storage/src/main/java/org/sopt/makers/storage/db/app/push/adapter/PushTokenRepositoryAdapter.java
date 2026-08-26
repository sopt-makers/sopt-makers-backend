package org.sopt.makers.storage.db.app.push.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.port.PushTokenRepositoryPort;
import org.sopt.makers.storage.db.app.push.entity.PushTokenEntity;
import org.sopt.makers.storage.db.app.push.repository.PushTokenJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PushTokenRepositoryAdapter implements PushTokenRepositoryPort {

  private final PushTokenJpaRepository pushTokenJpaRepository;

  @Override
  public boolean existsByUserIdAndToken(Long userId, String token) {
    return pushTokenJpaRepository.existsByUserIdAndToken(userId, token);
  }

  @Override
  public Optional<PushToken> findByUserIdAndToken(Long userId, String token) {
    return pushTokenJpaRepository
        .findByUserIdAndToken(userId, token)
        .map(PushTokenEntity::toDomain);
  }

  @Override
  public List<PushToken> findAllByUserId(Long userId) {
    return pushTokenJpaRepository.findAllByUserId(userId).stream()
        .map(PushTokenEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public PushToken save(PushToken pushToken) {
    return pushTokenJpaRepository.save(PushTokenEntity.from(pushToken)).toDomain();
  }

  @Override
  @Transactional
  public void deleteById(Long id) {
    pushTokenJpaRepository.deleteById(id);
  }
}
