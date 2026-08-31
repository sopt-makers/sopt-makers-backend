package org.sopt.makers.domain.crew.property.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.property.CrewProperty;
import org.sopt.makers.domain.crew.property.HomeContent;
import org.sopt.makers.domain.crew.property.exception.CrewPropertyException;
import org.sopt.makers.domain.crew.property.port.CrewPropertyRepositoryPort;

class CrewPropertyServiceTest {

  private final CrewPropertyRepositoryPort repository = mock(CrewPropertyRepositoryPort.class);
  private final CrewPropertyService service = new CrewPropertyService(repository);

  @Test
  @DisplayName("운영 home 프로퍼티 구조를 홈 컨텐츠로 변환한다")
  void convertsHomeProperty() {
    when(repository.findByKey("home"))
        .thenReturn(
            Optional.of(
                new CrewProperty(
                    1L,
                    "home",
                    Map.of(
                        "home", List.of(Map.of("title", "상단 모임", "meetingIds", List.of(1, 2L)))))));

    List<HomeContent> result = service.getHomeContents();

    assertThat(result).containsExactly(new HomeContent("상단 모임", List.of(1L, 2L)));
  }

  @Test
  @DisplayName("home 배열이 없는 프로퍼티는 거부한다")
  void rejectsInvalidHomeProperty() {
    when(repository.findByKey("home"))
        .thenReturn(Optional.of(new CrewProperty(1L, "home", Map.of("title", "잘못된 값"))));

    assertThatThrownBy(service::getHomeContents).isInstanceOf(CrewPropertyException.class);
  }

  @Test
  @DisplayName("키 없이 조회하면 모든 프로퍼티 값을 반환한다")
  void getsAllPropertyValues() {
    when(repository.findAll())
        .thenReturn(
            List.of(
                new CrewProperty(1L, "home", Map.of("home", List.of())),
                new CrewProperty(2L, "officialDateText", Map.of("officialDateText", "기간"))));

    assertThat(service.getAllValues())
        .containsExactly(Map.of("home", List.of()), Map.of("officialDateText", "기간"));
  }
}
