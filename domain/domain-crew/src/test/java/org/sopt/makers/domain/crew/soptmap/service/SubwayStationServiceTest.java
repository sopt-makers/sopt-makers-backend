package org.sopt.makers.domain.crew.soptmap.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.soptmap.SubwayStation;
import org.sopt.makers.domain.crew.soptmap.exception.SoptMapException;
import org.sopt.makers.domain.crew.soptmap.port.SubwayStationRepositoryPort;

class SubwayStationServiceTest {

  private final SubwayStationRepositoryPort repository = mock(SubwayStationRepositoryPort.class);
  private final SubwayStationService service = new SubwayStationService(repository);

  @Test
  @DisplayName("요청한 지하철역 중 누락된 역이 있으면 예외가 발생한다")
  void rejectsMissingStation() {
    when(repository.findAllByNames(List.of("강남역", "없는역")))
        .thenReturn(List.of(new SubwayStation(1L, "강남역", List.of(), null, null)));

    assertThatThrownBy(() -> service.resolveStationIds(List.of("강남역", "없는역")))
        .isInstanceOf(SoptMapException.class);
  }
}
