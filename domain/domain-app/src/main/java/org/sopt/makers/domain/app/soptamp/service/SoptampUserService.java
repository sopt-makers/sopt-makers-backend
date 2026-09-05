package org.sopt.makers.domain.app.soptamp.service;

import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampPointUpdaterPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserCommandPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.rank.SoptampScoreCacheSyncEvent;
import org.sopt.makers.domain.app.soptamp.rank.port.RankCachePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SoptampUserService {

  private final SoptampUserQueryPort soptampUserQueryPort;
  private final SoptampUserCommandPort soptampUserCommandPort;
  private final SoptampPointUpdaterPort soptampPointUpdaterPort;
  private final RankCachePort rankCachePort;
  private final SoptampMode soptampMode;
  private final ApplicationEventPublisher eventPublisher;
  private final Long currentGeneration;

  public SoptampUserService(
      SoptampUserQueryPort soptampUserQueryPort,
      SoptampUserCommandPort soptampUserCommandPort,
      SoptampPointUpdaterPort soptampPointUpdaterPort,
      RankCachePort rankCachePort,
      SoptampMode soptampMode,
      ApplicationEventPublisher eventPublisher,
      @Value("${sopt.current.generation}") Long currentGeneration) {
    this.soptampUserQueryPort = soptampUserQueryPort;
    this.soptampUserCommandPort = soptampUserCommandPort;
    this.soptampPointUpdaterPort = soptampPointUpdaterPort;
    this.rankCachePort = rankCachePort;
    this.soptampMode = soptampMode;
    this.eventPublisher = eventPublisher;
    this.currentGeneration = currentGeneration;
  }

  public SoptampUser getSoptampUser(Long userId) {
    return soptampUserQueryPort
        .findByUserId(userId)
        .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
  }

  @Transactional
  public SoptampUser editProfileMessage(Long userId, String profileMessage) {
    return soptampUserCommandPort.updateProfileMessage(userId, profileMessage);
  }

  @Transactional
  public void addPointByLevel(Long userId, Integer level) {
    SoptampUser soptampUser = getSoptampUser(userId);
    soptampPointUpdaterPort.addPointByLevel(userId, level);
    raiseScoreCacheSyncEvent(soptampUser);
  }

  @Transactional
  public void subtractPointByLevel(Long userId, Integer level) {
    SoptampUser soptampUser = getSoptampUser(userId);
    soptampPointUpdaterPort.subtractPointByLevel(userId, level);
    raiseScoreCacheSyncEvent(soptampUser);
  }

  @Transactional
  public void initPoint(Long userId) {
    SoptampUser soptampUser = getSoptampUser(userId);
    soptampPointUpdaterPort.initPoint(userId);
    raiseScoreCacheSyncEvent(soptampUser);
  }

  @Transactional
  public void deleteAllSoptampUsers() {
    soptampUserCommandPort.deleteAll();
    rankCachePort.clearScores();
  }

  @Transactional
  public void initAllPoints() {
    soptampPointUpdaterPort.initAllPoints();
  }

  private void raiseScoreCacheSyncEvent(SoptampUser user) {
    if (soptampMode.isAppjam()) {
      return;
    }
    if (currentGeneration.equals(user.generation())) {
      eventPublisher.publishEvent(new SoptampScoreCacheSyncEvent(user.userId()));
    }
  }
}
