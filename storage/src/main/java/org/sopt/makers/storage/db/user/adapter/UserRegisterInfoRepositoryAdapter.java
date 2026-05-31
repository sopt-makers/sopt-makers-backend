package org.sopt.makers.storage.db.user.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.UserRegisterInfo;
import org.sopt.makers.domain.user.port.UserRegisterInfoRepositoryPort;
import org.sopt.makers.storage.db.user.entity.UserRegisterInfoEntity;
import org.sopt.makers.storage.db.user.repository.UserRegisterInfoJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRegisterInfoRepositoryAdapter implements UserRegisterInfoRepositoryPort {

  private final UserRegisterInfoJpaRepository userRegisterInfoJpaRepository;

  @Override
  public Optional<UserRegisterInfo> findByPhone(String phone) {
    return userRegisterInfoJpaRepository.findByPhone(phone).map(UserRegisterInfoEntity::toDomain);
  }

  @Transactional
  @Override
  public UserRegisterInfo save(UserRegisterInfo info) {
    UserRegisterInfoEntity entity =
        UserRegisterInfoEntity.create(
            info.name(),
            info.phone(),
            info.email(),
            info.birthday(),
            info.generation(),
            info.part());
    return userRegisterInfoJpaRepository.save(entity).toDomain();
  }

  @Transactional
  @Override
  public void delete(UserRegisterInfo info) {
    userRegisterInfoJpaRepository
        .findByPhone(info.phone())
        .ifPresent(userRegisterInfoJpaRepository::delete);
  }
}
