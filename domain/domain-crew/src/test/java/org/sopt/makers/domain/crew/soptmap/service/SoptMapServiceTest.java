package org.sopt.makers.domain.crew.soptmap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.soptmap.EventGift;
import org.sopt.makers.domain.crew.soptmap.MapRecommend;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMap;
import org.sopt.makers.domain.crew.soptmap.SoptMapEventPolicy;
import org.sopt.makers.domain.crew.soptmap.SoptMapUser;
import org.sopt.makers.domain.crew.soptmap.port.EventGiftRepositoryPort;
import org.sopt.makers.domain.crew.soptmap.port.MapRecommendRepositoryPort;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapEventPolicyPort;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapRepositoryPort;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapUserPort;

class SoptMapServiceTest {

  private final SoptMapRepositoryPort soptMapRepository = mock(SoptMapRepositoryPort.class);
  private final MapRecommendRepositoryPort recommendRepository =
      mock(MapRecommendRepositoryPort.class);
  private final EventGiftRepositoryPort giftRepository = mock(EventGiftRepositoryPort.class);
  private final SoptMapEventPolicyPort eventPolicyPort = mock(SoptMapEventPolicyPort.class);
  private final SoptMapUserPort userPort = mock(SoptMapUserPort.class);
  private final SubwayStationService stationService = mock(SubwayStationService.class);

  private final SoptMapService service =
      new SoptMapService(
          soptMapRepository,
          recommendRepository,
          giftRepository,
          eventPolicyPort,
          userPort,
          stationService);

  @Test
  @DisplayName("첫 장소를 등록하면 최초 등록 여부를 반환한다")
  void createsFirstSoptMap() {
    when(userPort.findById(1L)).thenReturn(Optional.of(new SoptMapUser(1L, "유저")));
    when(soptMapRepository.existsByPlaceName("온더플랜")).thenReturn(false);
    when(soptMapRepository.existsByCreatorId(1L)).thenReturn(false);
    when(stationService.resolveStationIds(List.of("강남역"))).thenReturn(List.of(10L));
    when(soptMapRepository.save(any())).thenReturn(soptMap(20L, 1L));

    SoptMapService.CreatedSoptMap result =
        service.create(new SoptMapService.CreateSoptMapCommand(values(), List.of("강남역")), 1L);

    assertThat(result.soptMapId()).isEqualTo(20L);
    assertThat(result.firstRegistered()).isTrue();
  }

  @Test
  @DisplayName("기존 추천을 다시 누르면 활성 상태를 토글한다")
  void togglesExistingRecommend() {
    when(soptMapRepository.existsById(20L)).thenReturn(true);
    when(recommendRepository.findByUserIdAndSoptMapId(1L, 20L))
        .thenReturn(Optional.of(new MapRecommend(30L, 1L, 20L, false, null, null)));
    when(recommendRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    SoptMapService.ToggleRecommendResult result = service.toggleRecommend(20L, 1L);

    assertThat(result.isRecommended()).isTrue();
  }

  @Test
  @DisplayName("이벤트 당첨 순번에 해당하면 선물을 선점한다")
  void winnerClaimsEventGift() {
    SoptMap map = soptMap(20L, 1L);
    EventGift gift = new EventGift(40L, null, null, "https://gift", true, true, null, null);
    when(soptMapRepository.existsByCreatorIdAndId(1L, 20L)).thenReturn(true);
    when(eventPolicyPort.findPolicy())
        .thenReturn(
            Optional.of(
                new SoptMapEventPolicy(
                    LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31), List.of(1))));
    when(soptMapRepository.findCreatedBetween(any(), any())).thenReturn(List.of(map));
    when(giftRepository.existsByUserId(1L)).thenReturn(false);
    when(giftRepository.findFirstClaimableForUpdate()).thenReturn(Optional.of(gift));
    when(giftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    boolean result = service.checkEventWinning(20L, 1L);

    assertThat(result).isTrue();
  }

  private SoptMap.Values values() {
    return new SoptMap.Values(
        "온더플랜", "장소가 좋아요", List.of(MapTag.CAFE), "https://naver", "https://kakao");
  }

  private SoptMap soptMap(Long id, Long creatorId) {
    return new SoptMap(
        id,
        List.of(10L),
        "온더플랜",
        "장소가 좋아요",
        List.of(MapTag.CAFE),
        "https://naver",
        "https://kakao",
        creatorId,
        null,
        null);
  }
}
