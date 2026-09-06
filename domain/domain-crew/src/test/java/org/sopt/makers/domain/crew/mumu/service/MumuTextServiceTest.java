package org.sopt.makers.domain.crew.mumu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sopt.makers.domain.crew.mumu.exception.MumuFailure.OVERLAPPED_MUMU_TEXT_PERIOD;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.mumu.MumuText;
import org.sopt.makers.domain.crew.mumu.exception.MumuException;
import org.sopt.makers.domain.crew.mumu.port.MumuTextRepositoryPort;

class MumuTextServiceTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 5, 9, 0);

  private MumuTextRepositoryPort repositoryPort;
  private MumuTextService service;

  @BeforeEach
  void setUp() {
    repositoryPort = mock(MumuTextRepositoryPort.class);
    service =
        new MumuTextService(
            repositoryPort, Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
  }

  @Test
  @DisplayName("현재 노출 기간에 해당하는 무무 텍스트를 반환한다")
  void getsCurrentText() {
    MumuText text = text(1L, "오늘의 무무");
    when(repositoryPort.findActiveAt(NOW)).thenReturn(Optional.of(text));

    assertThat(service.getCurrentText()).isEqualTo("오늘의 무무");
  }

  @Test
  @DisplayName("기존 노출 기간과 겹치는 무무 텍스트는 생성할 수 없다")
  void rejectsOverlappedPeriod() {
    LocalDateTime startDate = NOW.plusDays(1);
    LocalDateTime endDate = NOW.plusDays(2);
    when(repositoryPort.findOverlapping(null, startDate, endDate))
        .thenReturn(List.of(text(1L, "기존 무무")));

    assertThatThrownBy(
            () ->
                service.create(
                    new MumuTextService.CreateMumuTextCommand("새 무무", "기본", startDate, endDate)))
        .isInstanceOf(MumuException.class)
        .extracting("error")
        .isEqualTo(OVERLAPPED_MUMU_TEXT_PERIOD);
  }

  private MumuText text(Long id, String value) {
    return new MumuText(id, value, "기본", NOW.minusDays(1), NOW.plusDays(1), null, null);
  }
}
