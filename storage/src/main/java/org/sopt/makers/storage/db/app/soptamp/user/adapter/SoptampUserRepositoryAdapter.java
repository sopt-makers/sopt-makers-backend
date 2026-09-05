package org.sopt.makers.storage.db.app.soptamp.user.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.storage.db.app.soptamp.user.entity.SoptampUserEntity;
import org.sopt.makers.storage.db.app.soptamp.user.repository.SoptampUserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptampUserRepositoryAdapter implements SoptampUserQueryPort {

  private final SoptampUserJpaRepository soptampUserJpaRepository;

  @Override
  public Optional<SoptampUser> findByUserId(Long userId) {
    return soptampUserJpaRepository.findByUserId(userId).map(SoptampUserEntity::toDomain);
  }

  @Override
  public List<SoptampUser> findAllByUserIds(Collection<Long> userIds) {
    return soptampUserJpaRepository.findAllByUserIdIn(userIds).stream()
        .map(SoptampUserEntity::toDomain)
        .toList();
  }

  @Override
  public List<SoptampUser> findAllByGeneration(Long generation) {
    return soptampUserJpaRepository.findAllByGeneration(generation).stream()
        .map(SoptampUserEntity::toDomain)
        .toList();
  }

  @Override
  public Map<Long, SoptampUser> findByUserIdsAsMap(Collection<Long> userIds) {
    return soptampUserJpaRepository.findAllByUserIdIn(userIds).stream()
        .map(SoptampUserEntity::toDomain)
        .collect(Collectors.toMap(SoptampUser::userId, Function.identity()));
  }

  @Override
  public Optional<SoptampUser> findByNickname(String nickname) {
    return soptampUserJpaRepository.findByNickname(nickname).map(SoptampUserEntity::toDomain);
  }

  @Override
  public boolean existsByNickname(String nickname) {
    return soptampUserJpaRepository.existsByNickname(nickname);
  }

  @Override
  @Transactional
  public SoptampUser save(SoptampUser soptampUser) {
    if (soptampUser.id() == null) {
      return soptampUserJpaRepository.save(SoptampUserEntity.from(soptampUser)).toDomain();
    }
    SoptampUserEntity entity = soptampUserJpaRepository.findById(soptampUser.id()).orElseThrow();
    entity.apply(soptampUser);
    return entity.toDomain();
  }
}
