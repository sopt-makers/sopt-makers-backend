package org.sopt.makers.domain.official.visitor.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VisitorService {

  private final Set<String> visitedKeys = ConcurrentHashMap.newKeySet();

  public void markVisited(String ip, String userAgent) {
    visitedKeys.add(ip + userAgent);
  }

  public int getTodayCount() {
    return visitedKeys.size();
  }

  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void resetDailyVisitors() {
    visitedKeys.clear();
    log.info("방문자 수 초기화 완료");
  }
}
