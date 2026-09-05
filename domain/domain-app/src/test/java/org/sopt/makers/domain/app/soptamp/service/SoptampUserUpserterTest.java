package org.sopt.makers.domain.app.soptamp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.rank.SoptampScoreCacheSyncEvent;
import org.sopt.makers.domain.app.soptamp.support.InMemoryAppjamUserStore;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.User;

@DisplayName("SoptampUserUpserter 테스트")
class SoptampUserUpserterTest {

  private static final long CURRENT_GENERATION = 38L;

  private final InMemorySoptampUserStore store = new InMemorySoptampUserStore(CURRENT_GENERATION);
  private final InMemoryAppjamUserStore appjamUserStore = new InMemoryAppjamUserStore();
  private final List<Object> published = new ArrayList<>();

  @Test
  @DisplayName("신규 유저는 파트 약칭에 이름을 붙인 닉네임과 0점으로 생긴다")
  void createsNewUserWithPartPrefixedNickname() {
    SoptampUserUpserter upserter = upserter(false);

    upserter.upsertChunk(List.of(user(1L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, true))));

    SoptampUser created = store.findByUserId(1L).orElseThrow();
    assertThat(created.nickname()).isEqualTo("서버김솝트");
    assertThat(created.totalPoints()).isZero();
    assertThat(created.profileMessage()).isEmpty();
    assertThat(created.generation()).isEqualTo(38L);
    assertThat(created.part()).isEqualTo(SoptampPart.SERVER);
    assertThat(published).containsExactly(new SoptampScoreCacheSyncEvent(1L));
  }

  @Test
  @DisplayName("파트장은 파트장 약칭이 접두로 붙는다")
  void usesPartLeaderPrefix() {
    SoptampUserUpserter upserter = upserter(false);

    upserter.upsertChunk(
        List.of(user(1L, "김솝트", activity(38, Part.SERVER, Role.PART_LEADER, true))));

    assertThat(store.findByUserId(1L).orElseThrow().nickname()).isEqualTo("서버파트장김솝트");
  }

  @Test
  @DisplayName("닉네임이 겹치면 같은 청크 안에서도 접미사가 하나씩 붙는다")
  void appendsSuffixOnNicknameCollision() {
    SoptampUserUpserter upserter = upserter(false);
    store.save(new SoptampUser(1L, 1L, "", 5L, "서버김솝트", 37L, SoptampPart.SERVER));

    upserter.upsertChunk(
        List.of(
            user(2L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, true)),
            user(3L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, true))));

    assertThat(store.findByUserId(2L).orElseThrow().nickname()).isEqualTo("서버김솝트A");
    assertThat(store.findByUserId(3L).orElseThrow().nickname()).isEqualTo("서버김솝트B");
  }

  @Test
  @DisplayName("기수가 그대로면 점수도 닉네임도 건드리지 않는다")
  void keepsEverythingWhenGenerationUnchanged() {
    SoptampUserUpserter upserter = upserter(false);
    store.save(new SoptampUser(1L, 1L, "한마디", 30L, "안드김솝트", 38L, SoptampPart.ANDROID));

    upserter.upsertChunk(List.of(user(1L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, true))));

    SoptampUser kept = store.findByUserId(1L).orElseThrow();
    assertThat(kept.nickname()).isEqualTo("안드김솝트");
    assertThat(kept.totalPoints()).isEqualTo(30L);
    assertThat(kept.part()).isEqualTo(SoptampPart.ANDROID);
    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("기수가 바뀌면 점수가 0이 되고 닉네임과 파트가 갱신된다. 한마디는 남는다")
  void resetsPointsWhenGenerationChanged() {
    SoptampUserUpserter upserter = upserter(false);
    store.save(new SoptampUser(1L, 1L, "한마디", 30L, "안드김솝트", 37L, SoptampPart.ANDROID));

    upserter.upsertChunk(List.of(user(1L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, true))));

    SoptampUser updated = store.findByUserId(1L).orElseThrow();
    assertThat(updated.nickname()).isEqualTo("서버김솝트");
    assertThat(updated.totalPoints()).isZero();
    assertThat(updated.generation()).isEqualTo(38L);
    assertThat(updated.part()).isEqualTo(SoptampPart.SERVER);
    assertThat(updated.profileMessage()).isEqualTo("한마디");
    assertThat(published).containsExactly(new SoptampScoreCacheSyncEvent(1L));
  }

  @Test
  @DisplayName("기수가 비어 있는 행은 건드리지 않고 같은 청크의 다음 유저는 그대로 처리한다")
  void skipsUserWithNullGeneration() {
    SoptampUserUpserter upserter = upserter(false);
    store.save(new SoptampUser(1L, 1L, "한마디", 30L, "안드김솝트", null, SoptampPart.ANDROID));

    List<Long> skippedUserIds =
        upserter.upsertChunk(
            List.of(
                user(1L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, true)),
                user(2L, "이솝트", activity(38, Part.SERVER, Role.MEMBER, true))));

    assertThat(skippedUserIds).containsExactly(1L);
    SoptampUser untouched = store.findByUserId(1L).orElseThrow();
    assertThat(untouched.nickname()).isEqualTo("안드김솝트");
    assertThat(untouched.totalPoints()).isEqualTo(30L);
    assertThat(untouched.generation()).isNull();
    assertThat(untouched.part()).isEqualTo(SoptampPart.ANDROID);
    assertThat(store.findByUserId(2L).orElseThrow().nickname()).isEqualTo("서버이솝트");
    assertThat(published).containsExactly(new SoptampScoreCacheSyncEvent(2L));
  }

  @Test
  @DisplayName("일반 시즌은 정규 활동이 없는 유저를 건너뛴다")
  void skipsUserWithoutSoptActivity() {
    SoptampUserUpserter upserter = upserter(false);

    upserter.upsertChunk(
        List.of(user(1L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, false)), user(2L, "이솝트")));

    assertThat(store.findAllUserIds()).isEmpty();
  }

  @Test
  @DisplayName("일반 시즌의 기수는 정규 활동의 최대 기수를 쓴다")
  void usesLastSoptGeneration() {
    SoptampUserUpserter upserter = upserter(false);

    upserter.upsertChunk(
        List.of(
            user(
                1L,
                "김솝트",
                activity(37, Part.SERVER, Role.MEMBER, true),
                activity(38, Part.WEB, Role.MEMBER, false))));

    SoptampUser created = store.findByUserId(1L).orElseThrow();
    assertThat(created.generation()).isEqualTo(37L);
    assertThat(created.nickname()).isEqualTo("서버김솝트");
    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("앱잼 모드는 팀명 닉네임으로 바꾸고 캐시 동기화 이벤트를 내지 않는다")
  void migratesToAppjamTeamNickname() {
    SoptampUserUpserter upserter = upserter(true);
    store.save(new SoptampUser(1L, 1L, "한마디", 30L, "서버김솝트", 38L, SoptampPart.SERVER));
    appjamUserStore.save(new AppjamUser(1L, 1L, "비트", TeamNumber.FIRST));

    upserter.upsertChunk(List.of(user(1L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, false))));

    SoptampUser migrated = store.findByUserId(1L).orElseThrow();
    assertThat(migrated.nickname()).isEqualTo("비트김솝트");
    assertThat(migrated.totalPoints()).isZero();
    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("앱잼 팀이 없으면 기수 닉네임을 쓰고, 이미 앱잼 닉네임이면 그대로 둔다")
  void usesGenerationNicknameWithoutAppjamTeam() {
    SoptampUserUpserter upserter = upserter(true);
    store.save(new SoptampUser(1L, 1L, "", 30L, "서버김솝트", 38L, SoptampPart.SERVER));
    store.save(new SoptampUser(2L, 2L, "", 20L, "38기이솝트", 38L, SoptampPart.SERVER));

    upserter.upsertChunk(
        List.of(
            user(1L, "김솝트", activity(38, Part.SERVER, Role.MEMBER, false)),
            user(2L, "이솝트", activity(38, Part.SERVER, Role.MEMBER, false))));

    assertThat(store.findByUserId(1L).orElseThrow().nickname()).isEqualTo("38기김솝트");
    SoptampUser untouched = store.findByUserId(2L).orElseThrow();
    assertThat(untouched.nickname()).isEqualTo("38기이솝트");
    assertThat(untouched.totalPoints()).isEqualTo(20L);
  }

  @Test
  @DisplayName("앱잼 모드는 같은 기수에 정규·비정규 활동이 겹치면 정규 활동의 파트를 쓴다")
  void prefersSoptActivityOnSameGenerationInAppjam() {
    SoptampUserUpserter upserter = upserter(true);

    upserter.upsertChunk(
        List.of(
            user(
                1L,
                "김솝트",
                activity(38, Part.SERVER, Role.MEMBER, true),
                activity(38, Part.WEB, Role.MEMBER, false))));

    assertThat(store.findByUserId(1L).orElseThrow().part()).isEqualTo(SoptampPart.SERVER);
  }

  private SoptampUserUpserter upserter(boolean appjamMode) {
    return new SoptampUserUpserter(
        store,
        store,
        appjamUserStore,
        new SoptampMode(appjamMode),
        published::add,
        CURRENT_GENERATION);
  }

  private static User user(Long userId, String name, Activity... activities) {
    return User.createUser(
        userId,
        SocialAccount.of("social-" + userId, OAuthPlatform.APPLE),
        Profile.of(name, null, "01000000000", null),
        ActivityList.of(List.of(activities)),
        false);
  }

  private static Activity activity(int generation, Part part, Role role, boolean isSopt) {
    return Activity.of(null, generation, null, part, role, isSopt, null);
  }
}
