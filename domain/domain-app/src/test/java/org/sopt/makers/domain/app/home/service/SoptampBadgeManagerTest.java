package org.sopt.makers.domain.app.home.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.soptamp.exception.SoptampFailure.NOT_FOUND_SOPTAMP_USER;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.sopt.makers.domain.app.soptamp.service.SoptampUserService;
import org.sopt.makers.domain.app.soptamp.support.InMemoryRankCache;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;

@DisplayName("SoptampBadgeManager 테스트")
class SoptampBadgeManagerTest {

  private static final long CURRENT_GENERATION = 38L;
  private static final Map<Part, Long> ONE_MEMBER_PER_PART =
      Map.of(
          Part.PLAN,
          1L,
          Part.DESIGN,
          1L,
          Part.ANDROID,
          1L,
          Part.IOS,
          1L,
          Part.WEB,
          1L,
          Part.SERVER,
          1L);

  private InMemorySoptampUserStore store;

  @BeforeEach
  void setUp() {
    store = new InMemorySoptampUserStore(CURRENT_GENERATION);
    store.save(user(1L, "서버홍길동", 30L, SoptampPart.SERVER));
    store.save(user(2L, "웹김솝트", 50L, SoptampPart.WEB));
    store.save(user(3L, "회장이임원", 10L, SoptampPart.PRESIDENT));
  }

  @Test
  @DisplayName("내 파트의 파트 랭킹을 N위 로 준다")
  void showsPartRank() {
    AppServiceBadgeInfo badge = manager(false).acquireAppServiceBadgeInfo(1L);

    assertThat(badge.displayAlarmBadge()).isTrue();
    assertThat(badge.alarmBadge()).isEqualTo("2위");
  }

  @Test
  @DisplayName("앱잼 시즌이면 뱃지를 끈다")
  void disabledInAppjamSeason() {
    assertThat(manager(true).acquireAppServiceBadgeInfo(1L))
        .isEqualTo(AppServiceBadgeInfo.createWithAllDisabled());
  }

  @Test
  @DisplayName("파트 축이 없는 유저는 뱃지를 끈다")
  void disabledWhenNoRankingPart() {
    assertThat(manager(false).acquireAppServiceBadgeInfo(3L))
        .isEqualTo(AppServiceBadgeInfo.createWithAllDisabled());
  }

  @Test
  @DisplayName("솝탬프 유저가 없으면 NOT_FOUND_SOPTAMP_USER 예외가 발생한다")
  void throwsWhenSoptampUserMissing() {
    assertThatThrownBy(() -> manager(false).acquireAppServiceBadgeInfo(99L))
        .isInstanceOf(SoptampException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_SOPTAMP_USER);
  }

  private SoptampBadgeManager manager(boolean appjamMode) {
    SoptampMode mode = new SoptampMode(appjamMode);
    InMemoryRankCache cache = new InMemoryRankCache();
    SoptampUserService userService =
        new SoptampUserService(store, store, store, cache, mode, event -> {}, CURRENT_GENERATION);
    RankService rankService =
        new RankService(store, generation -> ONE_MEMBER_PER_PART, cache, mode, CURRENT_GENERATION);
    return new SoptampBadgeManager(userService, rankService, mode);
  }

  private static SoptampUser user(Long userId, String nickname, Long points, SoptampPart part) {
    return new SoptampUser(userId, userId, "", points, nickname, CURRENT_GENERATION, part);
  }
}
