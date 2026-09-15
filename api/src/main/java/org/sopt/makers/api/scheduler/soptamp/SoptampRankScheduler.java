package org.sopt.makers.api.scheduler.soptamp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sopt.soptamp.scheduler.rank-reload.enabled", havingValue = "true")
public class SoptampRankScheduler {

  private final RankService rankService;

  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void reloadRankCache() {
    log.info("솝탬프 랭킹 캐시 재적재 시작");
    rankService.reloadRankCache();
  }
}
