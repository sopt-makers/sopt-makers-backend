package org.sopt.makers.storage.db.app.soptamp.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampPointUpdaterPort;
import org.sopt.makers.storage.db.app.soptamp.user.entity.SoptampUserEntity;
import org.sopt.makers.storage.db.app.soptamp.user.repository.SoptampUserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class SoptampPointUpdaterAdapter implements SoptampPointUpdaterPort {

  private final SoptampUserJpaRepository soptampUserJpaRepository;

  @Override
  public void addPointByLevel(Long userId, int level) {
    getSoptampUser(userId).addPointsByLevel(level);
  }

  @Override
  public void subtractPointByLevel(Long userId, int level) {
    getSoptampUser(userId).subtractPointsByLevel(level);
  }

  @Override
  public void initPoint(Long userId) {
    getSoptampUser(userId).initTotalPoints();
  }

  @Override
  public void initAllPoints() {
    soptampUserJpaRepository.findAll().forEach(SoptampUserEntity::initTotalPoints);
  }

  private SoptampUserEntity getSoptampUser(Long userId) {
    return soptampUserJpaRepository
        .findByUserId(userId)
        .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
  }
}
