package org.sopt.makers.domain.official.visitor.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VisitorServiceTest {
  private final VisitorService service = new VisitorService();

  @Test
  @DisplayName("같은 IP와 사용자 에이전트는 하루에 한 번만 집계한다")
  void countsVisitorOnce() {
    service.markVisited("203.0.113.1", "browser");
    service.markVisited("203.0.113.1", "browser");
    assertThat(service.getTodayCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("IP가 다르면 서로 다른 방문자로 집계한다")
  void distinguishesIp() {
    service.markVisited("203.0.113.1", "browser");
    service.markVisited("203.0.113.2", "browser");
    assertThat(service.getTodayCount()).isEqualTo(2);
  }

  @Test
  @DisplayName("사용자 에이전트가 다르면 서로 다른 방문자로 집계한다")
  void distinguishesUserAgent() {
    service.markVisited("203.0.113.1", "browser1");
    service.markVisited("203.0.113.1", "browser2");
    assertThat(service.getTodayCount()).isEqualTo(2);
  }

  @Test
  @DisplayName("방문자 초기화 후 같은 방문자를 다시 집계할 수 있다")
  void resetsVisitors() {
    service.markVisited("203.0.113.1", "browser");
    service.resetDailyVisitors();
    assertThat(service.getTodayCount()).isZero();
    service.markVisited("203.0.113.1", "browser");
    assertThat(service.getTodayCount()).isEqualTo(1);
  }
}
