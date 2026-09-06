package org.sopt.makers.domain.app.soptamp.rank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.rank.PartRank;
import org.sopt.makers.domain.app.soptamp.rank.RankedScore;
import org.sopt.makers.domain.app.soptamp.rank.UserRank;
import org.sopt.makers.domain.app.soptamp.support.InMemoryRankCache;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;

@DisplayName("RankService 테스트")
class RankServiceTest {

  private static final long CURRENT_GENERATION = 38L;

  private final InMemorySoptampUserStore store = new InMemorySoptampUserStore(CURRENT_GENERATION);
  private final InMemoryRankCache cache = new InMemoryRankCache();

  @Test
  @DisplayName("캐시가 있으면 캐시 점수와 순서로 답하고 DB 재적재를 하지 않는다")
  void readsFromCache() {
    store.save(user(1L, "서버유저", 5L, SoptampPart.SERVER));
    store.save(user(2L, "안드유저", 5L, SoptampPart.ANDROID));
    cache.put(1L, 100L);
    cache.put(2L, 300L);

    List<UserRank> ranks = service(false).findCurrentRanks();

    assertThat(ranks)
        .extracting(UserRank::rank, UserRank::nickname, UserRank::point)
        .containsExactly(tuple(1, "안드유저", 300L), tuple(2, "서버유저", 100L));
    assertThat(cache.calls()).isEmpty();
  }

  @Test
  @DisplayName("캐시가 비면 DB 전원으로 답하고 캐시를 다시 채운다")
  void fallsBackToDatabaseAndRefillsCache() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    store.save(user(2L, "안드유저", 300L, SoptampPart.ANDROID));

    List<UserRank> ranks = service(false).findCurrentRanks();

    assertThat(ranks)
        .extracting(UserRank::rank, UserRank::nickname, UserRank::point)
        .containsExactly(tuple(1, "안드유저", 300L), tuple(2, "서버유저", 100L));
    assertThat(cache.calls()).containsExactly("putAll");
    assertThat(cache.getRanking()).extracting(RankedScore::userId).containsExactly(2L, 1L);
  }

  @Test
  @DisplayName("캐시 미스 전체 조회는 지난 기수 유저를 응답과 캐시 재적재에서 뺀다")
  void excludesPastGenerationOnCacheMiss() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    store.save(user(2L, "서버지난기수", 300L, SoptampPart.SERVER, CURRENT_GENERATION - 1));

    List<UserRank> ranks = service(false).findCurrentRanks();

    assertThat(ranks).extracting(UserRank::nickname).containsExactly("서버유저");
    assertThat(cache.getRanking()).extracting(RankedScore::userId).containsExactly(1L);
  }

  @Test
  @DisplayName("캐시 미스 파트 조회도 닉네임 접두가 같은 지난 기수 유저를 뺀다")
  void excludesPastGenerationOnPartCacheMiss() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    store.save(user(2L, "서버지난기수", 300L, SoptampPart.SERVER, CURRENT_GENERATION - 1));

    List<UserRank> ranks = service(false).findCurrentRanksByPart(Part.SERVER);

    assertThat(ranks).extracting(UserRank::nickname).containsExactly("서버유저");
  }

  @Test
  @DisplayName("캐시 히트 파트 조회는 part 컬럼으로 거르고 순위를 파트 안에서 1부터 매긴다")
  void filtersByPartColumnOnCacheHit() {
    store.save(user(1L, "서버유저", 0L, SoptampPart.SERVER));
    store.save(user(2L, "서버파트장유저", 0L, SoptampPart.SERVER_PART_LEADER));
    store.save(user(3L, "서버라고쓴디자이너", 0L, SoptampPart.DESIGN));
    cache.put(1L, 100L);
    cache.put(2L, 300L);
    cache.put(3L, 500L);

    List<UserRank> ranks = service(false).findCurrentRanksByPart(Part.SERVER);

    assertThat(ranks)
        .extracting(UserRank::rank, UserRank::nickname, UserRank::point)
        .containsExactly(tuple(1, "서버파트장유저", 300L), tuple(2, "서버유저", 100L));
  }

  @Test
  @DisplayName("캐시 미스 파트 조회는 닉네임 접두로 고르고 캐시를 채우지 않는다")
  void filtersByNicknamePrefixOnCacheMiss() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    store.save(user(2L, "서버라고쓴디자이너", 300L, SoptampPart.DESIGN));
    store.save(user(3L, "안드유저", 500L, SoptampPart.ANDROID));

    List<UserRank> ranks = service(false).findCurrentRanksByPart(Part.SERVER);

    assertThat(ranks)
        .extracting(UserRank::rank, UserRank::nickname)
        .containsExactly(tuple(1, "서버라고쓴디자이너"), tuple(2, "서버유저"));
    assertThat(cache.calls()).isEmpty();
  }

  @Test
  @DisplayName("솝탬프 랭킹을 제공하지 않는 파트는 캐시 상태와 무관하게 막힌다")
  void rejectsUnsupportedPart() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    cache.put(1L, 100L);

    assertThatThrownBy(() -> service(false).findCurrentRanksByPart(Part.ALL))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.UNSUPPORTED_RANKING_PART.getMessage());
  }

  @Test
  @DisplayName("캐시에는 남았는데 유저 행이 없으면 그 유저만 빼고 순위를 1부터 다시 매긴다")
  void skipsCachedUserWithoutRow() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    store.save(user(2L, "안드유저", 200L, SoptampPart.ANDROID));
    cache.put(1L, 100L);
    cache.put(2L, 200L);
    cache.put(99L, 300L);

    List<UserRank> ranks = service(false).findCurrentRanks();

    assertThat(ranks)
        .extracting(UserRank::rank, UserRank::nickname, UserRank::point)
        .containsExactly(tuple(1, "안드유저", 200L), tuple(2, "서버유저", 100L));
  }

  @Test
  @DisplayName("파트별 조회도 유저 행이 없는 것을 빼고 파트 안에서 순위를 이어 붙인다")
  void skipsCachedUserWithoutRowOnPartRanking() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    store.save(user(2L, "서버파트장유저", 200L, SoptampPart.SERVER_PART_LEADER));
    cache.put(1L, 100L);
    cache.put(2L, 200L);
    cache.put(99L, 300L);

    List<UserRank> ranks = service(false).findCurrentRanksByPart(Part.SERVER);

    assertThat(ranks)
        .extracting(UserRank::rank, UserRank::nickname, UserRank::point)
        .containsExactly(tuple(1, "서버파트장유저", 200L), tuple(2, "서버유저", 100L));
  }

  @Test
  @DisplayName("파트끼리의 랭킹은 캐시를 보지 않고 DB 전원으로 계산한다")
  void calculatesPartRankFromDatabase() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    store.save(user(2L, "안드유저", 200L, SoptampPart.ANDROID));
    cache.put(1L, 9999L);

    List<PartRank> ranks =
        service(false, Map.of(Part.SERVER, 10L, Part.ANDROID, 10L)).findAllPartRanks();

    assertThat(ranks)
        .extracting(PartRank::part, PartRank::rank, PartRank::points, PartRank::pointsDecimal)
        .contains(
            tuple("서버", 2, 10L, new BigDecimal("10.00")),
            tuple("안드", 1, 20L, new BigDecimal("20.00")));
  }

  @Test
  @DisplayName("전량 재적재는 통삭제 뒤에 채워 탈퇴 유저의 낡은 점수를 남기지 않는다")
  void reloadsRankCache() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    cache.put(99L, 300L);

    service(false).reloadRankCache();

    assertThat(cache.calls()).containsExactly("clearScores", "putAll");
    assertThat(cache.getRanking()).extracting(RankedScore::userId).containsExactly(1L);
  }

  @Test
  @DisplayName("전량 재적재는 지난 기수 유저를 캐시에 담지 않는다")
  void excludesPastGenerationOnReload() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    store.save(user(2L, "서버지난기수", 300L, SoptampPart.SERVER, CURRENT_GENERATION - 1));

    service(false).reloadRankCache();

    assertThat(cache.getRanking()).extracting(RankedScore::userId).containsExactly(1L);
  }

  @Test
  @DisplayName("앱잼 모드면 랭킹 조회 셋은 막히고 재적재는 조용히 아무 것도 하지 않는다")
  void blocksQueriesInAppjamSeason() {
    store.save(user(1L, "서버유저", 100L, SoptampPart.SERVER));
    cache.put(99L, 300L);
    RankService service = service(true);

    assertThatThrownBy(service::findCurrentRanks)
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.INVALID_APPJAM_SEASON_REQUEST.getMessage());
    assertThatThrownBy(() -> service.findCurrentRanksByPart(Part.SERVER))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.INVALID_APPJAM_SEASON_REQUEST.getMessage());
    assertThatThrownBy(service::findAllPartRanks)
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.INVALID_APPJAM_SEASON_REQUEST.getMessage());

    service.reloadRankCache();
    assertThat(cache.calls()).isEmpty();
  }

  private RankService service(boolean appjamMode) {
    return service(appjamMode, Map.of());
  }

  private RankService service(boolean appjamMode, Map<Part, Long> partMemberCounts) {
    return new RankService(
        store,
        generation -> partMemberCounts,
        cache,
        new SoptampMode(appjamMode),
        CURRENT_GENERATION);
  }

  private static SoptampUser user(
      Long userId, String nickname, Long totalPoints, SoptampPart part) {
    return user(userId, nickname, totalPoints, part, CURRENT_GENERATION);
  }

  private static SoptampUser user(
      Long userId, String nickname, Long totalPoints, SoptampPart part, long generation) {
    return new SoptampUser(userId, userId, "한마디", totalPoints, nickname, generation, part);
  }
}
