package org.sopt.makers.storage.db.app.soptamp.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserCommandPort;
import org.sopt.makers.storage.db.app.soptamp.user.entity.SoptampUserEntity;
import org.sopt.makers.storage.db.app.soptamp.user.repository.SoptampUserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class SoptampUserCommandAdapter implements SoptampUserCommandPort {

  private final SoptampUserJpaRepository soptampUserJpaRepository;

  @Override
  public SoptampUser updateProfileMessage(Long userId, String profileMessage) {
    SoptampUserEntity soptampUser = findByUserId(userId);
    soptampUser.apply(soptampUser.toDomain().withProfileMessage(profileMessage));
    return soptampUser.toDomain();
  }

  @Override
  public void create(Long userId, String nickname, Long generation, SoptampPart part) {
    soptampUserJpaRepository.save(
        SoptampUserEntity.from(SoptampUser.create(userId, nickname, generation, part)));
  }

  @Override
  public void updateChangedGenerationInfo(
      Long userId, Long generation, SoptampPart part, String nickname) {
    SoptampUserEntity soptampUser = findByUserId(userId);
    soptampUser.apply(soptampUser.toDomain().refreshForGeneration(nickname, generation, part));
  }

  @Override
  public void deleteAll() {
    soptampUserJpaRepository.deleteAllInBatch();
  }

  private SoptampUserEntity findByUserId(Long userId) {
    return soptampUserJpaRepository
        .findByUserId(userId)
        .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
  }
}
