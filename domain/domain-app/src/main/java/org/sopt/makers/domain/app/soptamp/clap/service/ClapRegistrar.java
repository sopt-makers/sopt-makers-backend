package org.sopt.makers.domain.app.soptamp.clap.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.sopt.makers.domain.app.soptamp.clap.ClapEvent;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapRepositoryPort;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClapRegistrar {

  private final ClapRepositoryPort clapRepositoryPort;
  private final StampRepositoryPort stampRepositoryPort;
  private final SoptampMode soptampMode;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int register(Long userId, Long stampId, int increment) {
    Stamp stamp =
        stampRepositoryPort
            .findById(stampId)
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_STAMP));
    if (Objects.equals(stamp.userId(), userId)) {
      throw new SoptampException(SoptampFailure.FORBIDDEN_SELF_CLAP);
    }

    Clap clap =
        clapRepositoryPort
            .findByUserIdAndStampId(userId, stampId)
            .orElseGet(() -> Clap.create(stampId, userId));
    int applied = clap.applicableIncrement(increment);
    if (applied <= 0) {
      return 0;
    }
    clapRepositoryPort.save(
        new Clap(
            clap.id(), clap.stampId(), clap.userId(), clap.clapCount() + applied, clap.version()));

    if (stampRepositoryPort.increaseClapCount(stampId, applied) != 1) {
      throw new SoptampException(SoptampFailure.NOT_FOUND_STAMP);
    }
    if (!soptampMode.isAppjam()) {
      int newClapTotal =
          stampRepositoryPort
              .findById(stampId)
              .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_STAMP))
              .clapCount();
      eventPublisher.publishEvent(
          new ClapEvent(stamp.userId(), stampId, newClapTotal - applied, newClapTotal));
    }
    return applied;
  }
}
