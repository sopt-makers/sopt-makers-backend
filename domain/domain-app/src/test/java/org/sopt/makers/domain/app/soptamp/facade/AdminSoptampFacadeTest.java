package org.sopt.makers.domain.app.soptamp.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.rank.RankedScore;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.sopt.makers.domain.app.soptamp.service.MissionService;
import org.sopt.makers.domain.app.soptamp.service.SoptampUserService;
import org.sopt.makers.domain.app.soptamp.service.StampService;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.support.FakePushSender;
import org.sopt.makers.domain.app.soptamp.support.InMemoryClapStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryMissionStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryRankCache;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryStampStore;
import org.sopt.makers.domain.app.soptamp.support.NoopStampFileStorage;
import org.springframework.data.domain.PageRequest;

@DisplayName("AdminSoptampFacade 테스트")
class AdminSoptampFacadeTest {

  private static final long CURRENT_GENERATION = 38L;

  private final InMemorySoptampUserStore userStore =
      new InMemorySoptampUserStore(CURRENT_GENERATION);
  private final InMemoryStampStore stampStore = new InMemoryStampStore();
  private final InMemoryClapStore clapStore = new InMemoryClapStore();
  private final InMemoryRankCache cache = new InMemoryRankCache();
  private final List<String> deletedImages = new ArrayList<>();

  @Test
  @DisplayName("전체 초기화는 박수와 스탬프와 솝탬프 유저를 모두 비우고 이미지도 함께 지운다")
  void clearsEverything() {
    givenStamp(1L, 10L, List.of("https://s3/1.png", "https://s3/2.png"));
    clapStore.save(new Clap(null, 1L, 20L, 3, 0L));
    clapStore.tryMarkFirstHit(1L, 1);
    userStore.save(user(10L, 100L));
    cache.put(10L, 100L);

    facade(false).clearSoptampData(true, true);

    assertThat(stampStore.findAll()).isEmpty();
    assertThat(clapStore.findAllByStampIdOrderByClapCountDesc(1L, PageRequest.of(0, 10))).isEmpty();
    assertThat(clapStore.tryMarkFirstHit(1L, 1)).isTrue();
    assertThat(userStore.findAllUserIds()).isEmpty();
    assertThat(cache.getRanking()).isEmpty();
    assertThat(deletedImages).containsExactly("https://s3/1.png", "https://s3/2.png");
  }

  @Test
  @DisplayName("스탬프만 지우면 솝탬프 유저와 점수 캐시는 그대로 남는다")
  void clearsStampOnly() {
    givenStamp(1L, 10L, List.of("https://s3/1.png"));
    userStore.save(user(10L, 100L));
    cache.put(10L, 100L);

    facade(false).clearSoptampData(true, false);

    assertThat(stampStore.findAll()).isEmpty();
    assertThat(userStore.findAllUserIds()).containsExactly(10L);
    assertThat(cache.getRanking()).extracting(RankedScore::userId).containsExactly(10L);
  }

  @Test
  @DisplayName("솝탬프 유저만 지우면 스탬프는 남고 이미지 삭제도 부르지 않는다")
  void clearsSoptampUserOnly() {
    givenStamp(1L, 10L, List.of("https://s3/1.png"));
    userStore.save(user(10L, 100L));

    facade(false).clearSoptampData(false, true);

    assertThat(stampStore.findAll()).hasSize(1);
    assertThat(userStore.findAllUserIds()).isEmpty();
    assertThat(deletedImages).isEmpty();
  }

  @Test
  @DisplayName("이미지가 하나도 없으면 S3 삭제를 부르지 않는다")
  void skipsImageDeleteWhenNoImage() {
    givenStamp(1L, 10L, List.of());

    facade(false).clearSoptampData(true, false);

    assertThat(deletedImages).isEmpty();
  }

  @Test
  @DisplayName("점수 초기화는 기수를 가리지 않고 0 으로 만든 뒤 캐시를 다시 채운다")
  void initsPointsAndReloadsCache() {
    userStore.save(user(1L, 100L));
    userStore.save(
        new SoptampUser(2L, 2L, "", 200L, "서버지난기수", CURRENT_GENERATION - 1, SoptampPart.SERVER));
    cache.put(1L, 100L);

    facade(false).initPoints();

    assertThat(userStore.findByUserId(1L).orElseThrow().totalPoints()).isZero();
    assertThat(userStore.findByUserId(2L).orElseThrow().totalPoints()).isZero();
    assertThat(cache.calls()).containsExactly("clearScores", "putAll");
    assertThat(cache.getRanking()).contains(new RankedScore(1L, 0L));
  }

  @Test
  @DisplayName("앱잼 모드면 점수만 0 이 되고 캐시는 손대지 않는다")
  void skipsCacheReloadOnInitPointsInAppjamSeason() {
    userStore.save(user(1L, 100L));
    cache.put(1L, 100L);

    facade(true).initPoints();

    assertThat(userStore.findByUserId(1L).orElseThrow().totalPoints()).isZero();
    assertThat(cache.calls()).isEmpty();
    assertThat(cache.getRanking()).containsExactly(new RankedScore(1L, 100L));
  }

  @Test
  @DisplayName("어드민 캐시 재적재는 통삭제 뒤에 현재 기수로 다시 채운다")
  void reloadsRankCache() {
    userStore.save(user(1L, 100L));
    cache.put(99L, 300L);

    facade(false).initRankCache();

    assertThat(cache.calls()).containsExactly("clearScores", "putAll");
    assertThat(cache.getRanking()).containsExactly(new RankedScore(1L, 100L));
  }

  @Test
  @DisplayName("앱잼 모드의 어드민 캐시 재적재는 스케줄러와 달리 막힌다")
  void blocksAdminRankCacheReloadInAppjamSeason() {
    userStore.save(user(1L, 100L));
    cache.put(99L, 300L);

    assertThatThrownBy(() -> facade(true).initRankCache())
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.INVALID_APPJAM_SEASON_REQUEST.getMessage());
    assertThat(cache.calls()).isEmpty();
  }

  private void givenStamp(Long stampId, Long userId, List<String> images) {
    stampStore.save(
        new Stamp(stampId, "내용", images, userId, 1L, "2026-08-27", null, null, 0, 0, 0L));
  }

  private AdminSoptampFacade facade(boolean appjamMode) {
    SoptampMode soptampMode = new SoptampMode(appjamMode);
    SoptampUserService soptampUserService =
        new SoptampUserService(
            userStore, userStore, userStore, cache, soptampMode, event -> {}, CURRENT_GENERATION);
    RankService rankService =
        new RankService(userStore, generation -> Map.of(), cache, soptampMode, CURRENT_GENERATION);
    return new AdminSoptampFacade(
        clapStore,
        clapStore,
        stampStore,
        deletedImages::addAll,
        soptampUserService,
        rankService,
        soptampMode,
        userStore,
        new MissionService(new InMemoryMissionStore(), stampStore),
        new FakePushSender(),
        new StampService(stampStore, new NoopStampFileStorage()));
  }

  private static SoptampUser user(Long userId, Long totalPoints) {
    return new SoptampUser(
        userId,
        userId,
        "한마디",
        totalPoints,
        "서버솝트" + userId,
        CURRENT_GENERATION,
        SoptampPart.SERVER);
  }
}
