package org.sopt.makers.storage.db.app.fortune.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.fortune.UserFortune;
import org.sopt.makers.domain.app.fortune.port.UserFortuneRepositoryPort;
import org.sopt.makers.storage.db.app.fortune.entity.UserFortuneEntity;
import org.sopt.makers.storage.db.app.fortune.repository.UserFortuneJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFortuneRepositoryAdapter implements UserFortuneRepositoryPort {

  private final UserFortuneJpaRepository userFortuneJpaRepository;

  @Override
  public Optional<UserFortune> findByUserId(Long userId) {
    return userFortuneJpaRepository.findByUserId(userId).map(UserFortuneEntity::toDomain);
  }

  @Override
  @Transactional
  public UserFortune save(UserFortune userFortune) {
    return userFortuneJpaRepository.save(UserFortuneEntity.from(userFortune)).toDomain();
  }
}
