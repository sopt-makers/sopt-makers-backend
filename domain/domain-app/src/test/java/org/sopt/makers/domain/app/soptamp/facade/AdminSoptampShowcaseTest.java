package org.sopt.makers.domain.app.soptamp.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.soptamp.exception.SoptampFailure.NOT_FOUND_SOPTAMP_USER;
import static org.sopt.makers.domain.app.soptamp.exception.SoptampFailure.NOT_FOUND_STAMP;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
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

@DisplayName("AdminSoptampFacade 쇼케이스 알림 테스트")
class AdminSoptampShowcaseTest {

  private static final long CURRENT_GENERATION = 38L;
  private static final Long OWNER = 1L;
  private static final Long MISSION_ID = 10L;

  private InMemorySoptampUserStore userStore;
  private InMemoryStampStore stampStore;
  private InMemoryMissionStore missionStore;
  private FakePushSender pushSender;
  private AdminSoptampFacade facade;

  @BeforeEach
  void setUp() {
    userStore = new InMemorySoptampUserStore(CURRENT_GENERATION);
    stampStore = new InMemoryStampStore();
    missionStore = new InMemoryMissionStore();
    pushSender = new FakePushSender();
    InMemoryClapStore clapStore = new InMemoryClapStore();
    InMemoryRankCache cache = new InMemoryRankCache();
    SoptampMode mode = new SoptampMode(false);
    facade =
        new AdminSoptampFacade(
            clapStore,
            clapStore,
            stampStore,
            urls -> {},
            new SoptampUserService(
                userStore, userStore, userStore, cache, mode, event -> {}, CURRENT_GENERATION),
            new RankService(userStore, generation -> Map.of(), cache, mode, CURRENT_GENERATION),
            mode,
            userStore,
            new MissionService(missionStore, stampStore),
            pushSender,
            new StampService(stampStore, new NoopStampFileStorage()));

    userStore.save(user(OWNER, "서버홍길동", SoptampPart.SERVER));
    userStore.save(user(2L, "웹김솝트", SoptampPart.WEB));
    userStore.save(user(3L, "기획이솝트", SoptampPart.PLAN));
    missionStore.save(new Mission(MISSION_ID, "세미나 후기", 2, true, List.of()));
  }

  @Test
  @DisplayName("주인을 뺀 전원과 주인에게 딥링크가 다른 알림을 두 번 보낸다")
  void sendsTwoMessages() {
    stampStore.save(
        new Stamp(
            100L,
            "내용",
            List.of("https://img/s.png"),
            OWNER,
            MISSION_ID,
            "2026-09-01",
            null,
            null,
            0,
            0,
            0L));

    facade.sendSoptampShowcase(MISSION_ID, "서버홍길동", "제목", "본문");

    List<PushMessage> sent = pushSender.sent();
    assertThat(sent).hasSize(2);
    assertThat(sent.get(0).userIds()).isEqualTo(Set.of(2L, 3L));
    assertThat(sent.get(0).deepLink())
        .isEqualTo(
            "soptamp/entire-part-ranking/part-ranking/missions/missionDetail?id=100&isMine=false&nickname=서버홍길동&part=서버&missionId=10&level=2&missionTitle=세미나 후기");
    assertThat(sent.get(1).userIds()).isEqualTo(Set.of(OWNER));
    assertThat(sent.get(1).deepLink()).contains("isMine=true");
    assertThat(sent).extracting(PushMessage::title).containsOnly("제목");
    assertThat(sent).extracting(PushMessage::category).containsOnly(NotificationCategory.NOTICE);
  }

  @Test
  @DisplayName("닉네임의 솝탬프 유저가 없으면 NOT_FOUND_SOPTAMP_USER 예외가 발생한다")
  void throwsWhenOwnerMissing() {
    assertThatThrownBy(() -> facade.sendSoptampShowcase(MISSION_ID, "없는닉네임", "제목", "본문"))
        .isInstanceOf(SoptampException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_SOPTAMP_USER);
    assertThat(pushSender.sent()).isEmpty();
  }

  @Test
  @DisplayName("주인이 그 미션을 인증하지 않았으면 NOT_FOUND_STAMP 예외가 발생한다")
  void throwsWhenStampMissing() {
    assertThatThrownBy(() -> facade.sendSoptampShowcase(MISSION_ID, "서버홍길동", "제목", "본문"))
        .isInstanceOf(SoptampException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_STAMP);
  }

  private static SoptampUser user(Long userId, String nickname, SoptampPart part) {
    return new SoptampUser(userId, userId, "", 0L, nickname, CURRENT_GENERATION, part);
  }
}
