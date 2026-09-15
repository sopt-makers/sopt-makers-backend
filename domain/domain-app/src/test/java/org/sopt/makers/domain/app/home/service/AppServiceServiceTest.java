package org.sopt.makers.domain.app.home.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.home.exception.HomeFailure.NOT_FOUND_APP_SERVICE;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.exception.HomeException;
import org.sopt.makers.domain.app.home.fake.InMemoryAppServiceRepositoryPort;

@DisplayName("AppServiceService 테스트")
class AppServiceServiceTest {

  private InMemoryAppServiceRepositoryPort appServiceRepositoryPort;
  private AppServiceService appServiceService;

  @BeforeEach
  void setUp() {
    appServiceRepositoryPort = new InMemoryAppServiceRepositoryPort();
    appServiceService = new AppServiceService(appServiceRepositoryPort);
  }

  @Test
  @DisplayName("OTHERS, FLOATING_BUTTON, REVIEW_FORM, 미등록 서비스는 전체 목록에서 빠진다")
  void hidesInternalServices() {
    addService("POKE", 1);
    addService("SOPTAMP", 2);
    addService("OTHERS", 3);
    addService("FLOATING_BUTTON", 4);
    addService("REVIEW_FORM", 5);
    addService("UNKNOWN_SERVICE", 7);

    assertThat(appServiceService.getAllAppService())
        .extracting(AppService::serviceName)
        .containsExactly("SOPTAMP", "POKE");
  }

  @Test
  @DisplayName("전체 목록은 createdAt 내림차순이다")
  void sortsByCreatedAtDesc() {
    addService("POKE", 1);
    addService("SOPT_LETTER", 3);
    addService("SOPTAMP", 2);

    assertThat(appServiceService.getAllAppService())
        .extracting(AppService::serviceName)
        .containsExactly("SOPT_LETTER", "SOPTAMP", "POKE");
  }

  @Test
  @DisplayName("홈은 SOPT_LETTER 만, 탭은 POKE 와 SOPTAMP 만 준다")
  void filtersHomeAndTab() {
    addService("POKE", 1);
    addService("SOPTAMP", 2);
    addService("SOPT_LETTER", 3);

    assertThat(appServiceService.getHomeAppServices())
        .extracting(AppService::serviceName)
        .containsExactly("SOPT_LETTER");
    assertThat(appServiceService.getTabAppServices())
        .extracting(AppService::serviceName)
        .containsExactly("SOPTAMP", "POKE");
  }

  @Test
  @DisplayName("이름으로 못 찾으면 NOT_FOUND_APP_SERVICE 예외가 발생한다")
  void throwsWhenServiceMissing() {
    assertThatThrownBy(() -> appServiceService.getAppService("FLOATING_BUTTON"))
        .isInstanceOf(HomeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_APP_SERVICE);
  }

  private void addService(String serviceName, int day) {
    appServiceRepositoryPort.add(
        new AppService(
            (long) day,
            serviceName,
            true,
            false,
            "https://img/" + serviceName,
            "sopt://" + serviceName,
            LocalDateTime.of(2026, 9, day, 0, 0)));
  }
}
