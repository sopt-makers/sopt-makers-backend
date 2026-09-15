package org.sopt.makers.domain.app.soptamp.clap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.clap.ClapEvent;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.port.MissionRepositoryPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampProfileSourcePort;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.support.InMemoryClapStore;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryStampStore;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;

@DisplayName("ClapEventListener 테스트")
class ClapEventListenerTest {

  private static final Long OWNER_ID = 1L;
  private static final Long STAMP_ID = 10L;
  private static final Long MISSION_ID = 100L;

  private final InMemoryClapStore milestones = new InMemoryClapStore();
  private final InMemoryStampStore stamps = new InMemoryStampStore();
  private final InMemorySoptampUserStore soptampUsers = new InMemorySoptampUserStore(38L);
  private final FakeMissionStore missions = new FakeMissionStore();
  private final FakeProfileSource profileSource = new FakeProfileSource();
  private final List<PushMessage> sent = new ArrayList<>();
  private final PushSenderPort pushSender =
      new PushSenderPort() {
        @Override
        public void send(PushMessage message) {
          sent.add(message);
        }

        @Override
        public void register(PushToken pushToken) {
          throw new UnsupportedOperationException();
        }

        @Override
        public void delete(PushToken pushToken) {
          throw new UnsupportedOperationException();
        }
      };

  private final ClapEventListener listener =
      new ClapEventListener(milestones, stamps, missions, soptampUsers, profileSource, pushSender);

  @BeforeEach
  void setUp() {
    stamps.save(
        new Stamp(
            STAMP_ID, "내용", List.of(), OWNER_ID, MISSION_ID, "2026-08-27", null, null, 0, 0, 0L));
    missions.mission = new Mission(MISSION_ID, "동아리방 청소하기", 2, true, List.of());
    soptampUsers.save(
        new SoptampUser(OWNER_ID, OWNER_ID, "", 0L, "안드홍길동", 38L, SoptampPart.ANDROID));
    profileSource.owner = user(Part.ANDROID);
  }

  @Test
  @DisplayName("넘긴 구간이 없으면 알림 재료를 읽지도 않는다")
  void sendsNothingWhenNoMilestoneCrossed() {
    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 1, 7));

    assertThat(sent).isEmpty();
    assertThat(missions.callCount).isZero();
  }

  @Test
  @DisplayName("0에서 1000으로 뛰면 첫 박수와 100과 1000 세 건이 나간다")
  void sendsThreeMilestonesAtOnce() {
    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 0, 1000));

    assertThat(sent).hasSize(3);
    assertThat(sent.get(0).title()).isEqualTo("첫 박수 도착! 💌 ‘동아리방 청소하기’ 에 누군가가 박수를 쳤어요 👀");
    assertThat(sent.get(1).title()).isEqualTo("축하해요! 100번째 박수를 받았어요 🎉");
    assertThat(sent.get(2).title()).isEqualTo("박수 누적 1000개 🎉 ‘동아리방 청소하기’에 박수 갈채를 받고 있어요.");
    assertThat(sent).allSatisfy(message -> assertThat(message.userIds()).containsExactly(OWNER_ID));
    assertThat(sent)
        .allSatisfy(message -> assertThat(message.category()).isEqualTo(NotificationCategory.NEWS));
  }

  @Test
  @DisplayName("이미 마킹된 구간은 건너뛰고 그다음 구간이 나간다")
  void doesNotResendMarkedMilestone() {
    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 0, 1000));
    sent.clear();

    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 0, 3000));

    assertThat(sent.stream().map(PushMessage::title))
        .containsExactly("축하해요! 500번째 박수를 받았어요 🎉", "박수 누적 2000개 🎉 ‘동아리방 청소하기’에 박수 갈채를 받고 있어요.");
  }

  @Test
  @DisplayName("100과 500을 함께 넘겨도 100만 나간다")
  void sendsOnlyHundredWhenBothCrossed() {
    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 50, 600));

    assertThat(sent).hasSize(1);
    assertThat(sent.getFirst().title()).isEqualTo("축하해요! 100번째 박수를 받았어요 🎉");
    assertThat(sent.getFirst().content())
        .isEqualTo(
            """
            안드로이드파트 홍길동님의 ‘동아리방 청소하기’ 미션 사진이 100번째 박수를 받았습니다. 짝짝짝짝! 👏

            정말 대단해요! 앞으로도 계속해서 멋진 미션을 인증하고 파트/개인 랭킹을 올려보세요.

            어떤 솝트인이 박수쳤는 지 확인할 수 있어요!

            서로에게 응원의 박수를 보내며 소통해 보세요!
            """);
  }

  @Test
  @DisplayName("딥링크는 파트 약칭과 닉네임을 인코딩 없이 그대로 잇는다")
  void buildsDeepLinkWithShortPartName() {
    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 0, 1));

    assertThat(sent.getFirst().deepLink())
        .isEqualTo(
            "soptamp/entire-part-ranking/part-ranking/missions/missionDetail"
                + "?id=10&isMine=true&nickname=안드홍길동&part=안드&missionId=100&level=2"
                + "&missionTitle=동아리방 청소하기");
    assertThat(sent.getFirst().webLink()).isNull();
  }

  @Test
  @DisplayName("활동 이력이 없으면 파트를 정할 수 없어 알림을 포기한다")
  void failsWhenOwnerHasNoActivity() {
    profileSource.owner = user(null);

    assertThatThrownBy(() -> listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 0, 1)))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.NOT_FOUND_USER_PART.getMessage());
    assertThat(sent).isEmpty();
  }

  @Test
  @DisplayName("파트장은 본문에 직책 풀네임이, 딥링크에 직책 약칭이 실린다")
  void usesPartLeaderLabel() {
    profileSource.owner = user(Part.SERVER, Role.PART_LEADER, null);

    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 50, 100));

    assertThat(sent.getFirst().content()).startsWith("서버 파트장파트 홍길동님의");
    assertThat(sent.getFirst().deepLink()).contains("&part=서버파트장&");
  }

  @Test
  @DisplayName("회장처럼 활동 파트가 없는 임원진도 직책으로 표기한다")
  void usesExecutiveLabel() {
    profileSource.owner = user(null, Role.PRESIDENT, null);

    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 50, 100));

    assertThat(sent.getFirst().content()).startsWith("회장파트 홍길동님의");
    assertThat(sent.getFirst().deepLink()).contains("&part=회장&");
  }

  @Test
  @DisplayName("팀장은 팀으로 직책을 정한다")
  void usesTeamLeaderLabel() {
    profileSource.owner = user(Part.SERVER, Role.TEAM_LEADER, Team.MAKERS);

    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 50, 100));

    assertThat(sent.getFirst().content()).startsWith("메이커스 팀장파트 홍길동님의");
    assertThat(sent.getFirst().deepLink()).contains("&part=메팀장&");
  }

  @Test
  @DisplayName("알림 재료를 읽다가 터지면 아직 보내지 않은 구간은 마킹되지 않는다")
  void doesNotMarkUnsentMilestonesWhenLoadFails() {
    Mission mission = missions.mission;
    missions.mission = null;

    assertThatThrownBy(() -> listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 0, 1000)))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.NOT_FOUND_MISSION.getMessage());
    assertThat(sent).isEmpty();

    missions.mission = mission;
    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 0, 1000));

    assertThat(sent.stream().map(PushMessage::title))
        .containsExactly("축하해요! 100번째 박수를 받았어요 🎉", "박수 누적 1000개 🎉 ‘동아리방 청소하기’에 박수 갈채를 받고 있어요.");
  }

  @Test
  @DisplayName("같은 기수에 정규·비정규 활동이 함께 있으면 정규 활동 파트로 알린다")
  void prefersSoptActivityOnSameGeneration() {
    profileSource.owner =
        userWithActivities(
            List.of(
                Activity.of(null, 38, null, Part.SERVER, Role.MEMBER, true, null),
                Activity.of(null, 38, null, Part.PM, Role.MEMBER, false, null)));

    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 50, 100));

    assertThat(sent.getFirst().content()).startsWith("서버파트 홍길동님의");
    assertThat(sent.getFirst().deepLink()).contains("&part=서버&");
  }

  @Test
  @DisplayName("정규 활동이 더 낮은 기수면 최고 기수의 비정규 활동을 쓴다")
  void usesHighestGenerationEvenIfNotSopt() {
    profileSource.owner =
        userWithActivities(
            List.of(
                Activity.of(null, 38, null, Part.SERVER, Role.MEMBER, true, null),
                Activity.of(null, 39, null, Part.PM, Role.MEMBER, false, null)));

    listener.handle(new ClapEvent(OWNER_ID, STAMP_ID, 50, 100));

    assertThat(sent.getFirst().content()).startsWith("미상파트 홍길동님의");
    assertThat(sent.getFirst().deepLink()).contains("&part=선배&");
  }

  private User user(Part part) {
    return user(part, Role.MEMBER, null);
  }

  private User userWithActivities(List<Activity> activities) {
    return User.createUser(
        OWNER_ID,
        SocialAccount.of("platform-1", OAuthPlatform.GOOGLE),
        Profile.of("홍길동", "hong@sopt.org", "01000000000", null, null),
        ActivityList.of(activities),
        false);
  }

  private User user(Part part, Role role, Team team) {
    ActivityList activities =
        part == null && role == Role.MEMBER
            ? new ActivityList()
            : ActivityList.of(List.of(Activity.of(null, 38, team, part, role, true, null)));
    return User.createUser(
        OWNER_ID,
        SocialAccount.of("platform-1", OAuthPlatform.GOOGLE),
        Profile.of("홍길동", "hong@sopt.org", "01000000000", null, null),
        activities,
        false);
  }

  private static final class FakeMissionStore implements MissionRepositoryPort {

    private Mission mission;
    private int callCount = 0;

    @Override
    public Optional<Mission> findById(Long missionId) {
      callCount++;
      return Optional.ofNullable(mission).filter(it -> it.id().equals(missionId));
    }

    @Override
    public Mission save(Mission mission) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<Mission> findAllByDisplay(boolean display) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<Mission> findAllByDisplayOrderByLevelAscTitleAsc(boolean display) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<Mission> findByIdsOrderByLevelAndTitle(List<Long> missionIds) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<Mission> findDisplayedByIdsOrderByLevelAndTitle(List<Long> missionIds) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
      throw new UnsupportedOperationException();
    }
  }

  private static final class FakeProfileSource implements SoptampProfileSourcePort {

    private User owner;

    @Override
    public List<User> findAllForUpsert() {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findAllByUserIds(List<Long> userIds) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
      return Optional.ofNullable(owner).filter(it -> it.id().equals(userId));
    }
  }
}
