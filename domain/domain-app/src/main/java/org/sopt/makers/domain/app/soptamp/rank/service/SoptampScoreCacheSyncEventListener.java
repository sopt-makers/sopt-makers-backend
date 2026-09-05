package org.sopt.makers.domain.app.soptamp.rank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.rank.SoptampScoreCacheSyncEvent;
import org.sopt.makers.domain.app.soptamp.rank.port.RankCachePort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoptampScoreCacheSyncEventListener {

  private static final String CACHE_SYNC_EXECUTOR = "cacheSyncTaskExecutor";

  private final SoptampUserQueryPort soptampUserQueryPort;
  private final RankCachePort rankCachePort;

  @Async(CACHE_SYNC_EXECUTOR)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(final SoptampScoreCacheSyncEvent event) {
    try {
      soptampUserQueryPort
          .findByUserId(event.userId())
          .ifPresent(user -> rankCachePort.updateScore(user.userId(), user.totalPoints()));
    } catch (Exception e) {
      log.error("솝탬프 점수 캐시 동기화 실패. userId={}", event.userId(), e);
    }
  }
}
