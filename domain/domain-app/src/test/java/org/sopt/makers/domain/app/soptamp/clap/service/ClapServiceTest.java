package org.sopt.makers.domain.app.soptamp.clap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.sopt.makers.domain.app.soptamp.clap.ClapEvent;
import org.sopt.makers.domain.app.soptamp.clap.ClapUserProfile;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampProfileSourcePort;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.support.InMemoryClapStore;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryStampStore;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DisplayName("ClapService 테스트")
class ClapServiceTest {

  private static final Long OWNER_ID = 1L;
  private static final Long CLAPPER_ID = 2L;
  private static final Long STAMP_ID = 10L;

  private final InMemoryClapStore claps = new InMemoryClapStore();
  private final InMemoryStampStore stamps = new InMemoryStampStore();
  private final InMemorySoptampUserStore soptampUsers = new InMemorySoptampUserStore(38L);
  private final FakeProfileSource profileSource = new FakeProfileSource();
  private final List<Object> published = new ArrayList<>();

  @Test
  @DisplayName("자기 스탬프에는 박수를 칠 수 없다")
  void rejectsSelfClap() {
    stamps.save(stamp(0));

    assertThatThrownBy(() -> service(false).addClap(OWNER_ID, STAMP_ID, 1))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.FORBIDDEN_SELF_CLAP.getMessage());
  }

  @Test
  @DisplayName("증가량이 0 이하면 스탬프를 읽기도 전에 막는다")
  void rejectsNonPositiveIncrement() {
    assertThatThrownBy(() -> service(false).addClap(CLAPPER_ID, STAMP_ID, 0))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.INVALID_CLAP_COUNT.getMessage());
  }

  @Test
  @DisplayName("상한 50을 넘는 요청은 남은 만큼만 반영하고 총합도 그만큼만 올린다")
  void appliesOnlyUpToMaxClapCount() {
    stamps.save(stamp(300));
    claps.save(new Clap(1L, STAMP_ID, CLAPPER_ID, 40, 0L));

    int applied = service(false).addClap(CLAPPER_ID, STAMP_ID, 20);

    assertThat(applied).isEqualTo(10);
    assertThat(claps.findByUserIdAndStampId(CLAPPER_ID, STAMP_ID).orElseThrow().clapCount())
        .isEqualTo(50);
    assertThat(stamps.findById(STAMP_ID).orElseThrow().clapCount()).isEqualTo(310);
    assertThat(published).containsExactly(new ClapEvent(OWNER_ID, STAMP_ID, 300, 310));
  }

  @Test
  @DisplayName("동시 박수로 총합이 먼저 올라가면 갱신 뒤 실측값으로 구간을 판정한다")
  void publishesMeasuredTotalWhenClapsInterleave() {
    stamps.save(stamp(98));
    stamps.interleaveIncreaseBefore(1);

    int applied = service(false).addClap(CLAPPER_ID, STAMP_ID, 1);

    assertThat(applied).isEqualTo(1);
    assertThat(stamps.findById(STAMP_ID).orElseThrow().clapCount()).isEqualTo(100);
    assertThat(published).containsExactly(new ClapEvent(OWNER_ID, STAMP_ID, 99, 100));
  }

  @Test
  @DisplayName("이전 총합은 실측 총합에서 이번에 반영한 양을 뺀 값이다")
  void publishesOldTotalAsMeasuredTotalMinusApplied() {
    stamps.save(stamp(300));
    claps.save(new Clap(1L, STAMP_ID, CLAPPER_ID, 40, 0L));
    stamps.interleaveIncreaseBefore(7);

    int applied = service(false).addClap(CLAPPER_ID, STAMP_ID, 20);

    assertThat(applied).isEqualTo(10);
    assertThat(published).containsExactly(new ClapEvent(OWNER_ID, STAMP_ID, 307, 317));
  }

  @Test
  @DisplayName("이미 상한이면 총합도 이벤트도 그대로다")
  void skipsWhenAlreadyAtMax() {
    stamps.save(stamp(300));
    claps.save(new Clap(1L, STAMP_ID, CLAPPER_ID, 50, 0L));

    int applied = service(false).addClap(CLAPPER_ID, STAMP_ID, 5);

    assertThat(applied).isZero();
    assertThat(stamps.findById(STAMP_ID).orElseThrow().clapCount()).isEqualTo(300);
    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("앱잼 모드면 총합은 올라가도 알림 이벤트를 내지 않는다")
  void skipsEventInAppjamMode() {
    stamps.save(stamp(0));

    int applied = service(true).addClap(CLAPPER_ID, STAMP_ID, 3);

    assertThat(applied).isEqualTo(3);
    assertThat(stamps.findById(STAMP_ID).orElseThrow().clapCount()).isEqualTo(3);
    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("낙관적 락과 유니크 위반은 시도 전체를 다시 돌려 푼다")
  void retriesOnConflict() {
    stamps.save(stamp(0));
    claps.failNextSaves(
        new DataIntegrityViolationException("uk_clap_stamp_user"),
        new OptimisticLockingFailureException("version"));

    int applied = service(false).addClap(CLAPPER_ID, STAMP_ID, 3);

    assertThat(applied).isEqualTo(3);
    assertThat(stamps.findById(STAMP_ID).orElseThrow().clapCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("세 번째 시도까지 충돌하면 그 예외를 그대로 던진다")
  void throwsAfterMaxRetry() {
    stamps.save(stamp(0));
    claps.failNextSaves(
        new OptimisticLockingFailureException("1"),
        new OptimisticLockingFailureException("2"),
        new OptimisticLockingFailureException("3"));

    assertThatThrownBy(() -> service(false).addClap(CLAPPER_ID, STAMP_ID, 3))
        .isInstanceOf(OptimisticLockingFailureException.class)
        .hasMessageContaining("3");
  }

  @Test
  @DisplayName("유니크 위반은 낙관적 락 재시도 예산을 깎지 않는다")
  void countsUniqueViolationApartFromOptimisticLock() {
    stamps.save(stamp(0));
    claps.failNextSaves(
        new DataIntegrityViolationException("uk_clap_stamp_user"),
        new DataIntegrityViolationException("uk_clap_stamp_user"),
        new OptimisticLockingFailureException("1"),
        new OptimisticLockingFailureException("2"));

    int applied = service(false).addClap(CLAPPER_ID, STAMP_ID, 3);

    assertThat(applied).isEqualTo(3);
  }

  @Test
  @DisplayName("유니크 위반도 세 번째면 루프를 끊고 그 예외를 던진다")
  void throwsAfterMaxUniqueRetry() {
    stamps.save(stamp(0));
    claps.failNextSaves(
        new DataIntegrityViolationException("1"),
        new DataIntegrityViolationException("2"),
        new DataIntegrityViolationException("3"));

    assertThatThrownBy(() -> service(false).addClap(CLAPPER_ID, STAMP_ID, 3))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("3");
  }

  @Test
  @DisplayName("총합 증가가 0행이면 재시도 없이 스탬프 없음으로 끝낸다")
  void failsWhenClapTotalUpdateHitsNoRow() {
    stamps.save(stamp(0));
    stamps.failNextIncrease();

    assertThatThrownBy(() -> service(false).addClap(CLAPPER_ID, STAMP_ID, 3))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.NOT_FOUND_STAMP.getMessage());
    assertThat(published).isEmpty();
    assertThat(stamps.findById(STAMP_ID).orElseThrow().clapCount()).isZero();
  }

  @Test
  @DisplayName("남의 스탬프 박수 목록은 열리지 않는다")
  void rejectsClapListOfOthersStamp() {
    stamps.save(stamp(0));

    assertThatThrownBy(
            () -> service(false).getClapsOfMyStamp(CLAPPER_ID, STAMP_ID, PageRequest.of(0, 25)))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.FORBIDDEN_CLAP_LIST.getMessage());
  }

  @Test
  @DisplayName("박수 목록은 박수 수 내림차순이고 유저 정보를 벌크로 채운다")
  void fillsClapListWithBulkUserInfo() {
    stamps.save(stamp(0));
    claps.save(new Clap(1L, STAMP_ID, 2L, 3, 0L));
    claps.save(new Clap(2L, STAMP_ID, 3L, 12, 0L));
    soptampUsers.save(soptampUser(2L, "서버이지훈", "뒹굴뒹굴 ~,~"));
    soptampUsers.save(soptampUser(3L, "안드김솝트", "안녕하세요"));
    profileSource.save(user(2L, "이지훈", "https://cdn.sopt.org/2.jpg"));
    profileSource.save(user(3L, "김솝트", null));

    Page<ClapUserProfile> page =
        service(false).getClapsOfMyStamp(OWNER_ID, STAMP_ID, PageRequest.of(0, 1));

    assertThat(page.getContent()).containsExactly(new ClapUserProfile("안드김솝트", "", "안녕하세요", 12));
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getSize()).isEqualTo(1);
    assertThat(page.getNumber()).isZero();
    assertThat(profileSource.bulkCallCount).isEqualTo(1);
  }

  private ClapService service(boolean appjamMode) {
    ClapRegistrar registrar =
        new ClapRegistrar(claps, stamps, new SoptampMode(appjamMode), published::add);
    return new ClapService(registrar, claps, stamps, soptampUsers, profileSource);
  }

  private Stamp stamp(int clapCount) {
    return new Stamp(
        STAMP_ID, "내용", List.of(), OWNER_ID, 100L, "2026-08-27", null, null, clapCount, 0, 0L);
  }

  private SoptampUser soptampUser(Long userId, String nickname, String profileMessage) {
    return new SoptampUser(userId, userId, profileMessage, 0L, nickname, 38L, SoptampPart.SERVER);
  }

  private User user(Long userId, String name, String profileImage) {
    return User.createUser(
        userId,
        SocialAccount.of("platform-" + userId, OAuthPlatform.GOOGLE),
        Profile.of(name, name + "@sopt.org", "01000000000", null, profileImage),
        ActivityList.of(List.of(Activity.of(38, null, Part.SERVER, true))),
        false);
  }

  private static final class FakeProfileSource implements SoptampProfileSourcePort {

    private final List<User> users = new ArrayList<>();
    private int bulkCallCount = 0;

    void save(User user) {
      users.add(user);
    }

    @Override
    public List<User> findAllForUpsert() {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findAllByUserIds(List<Long> userIds) {
      bulkCallCount++;
      return users.stream().filter(it -> userIds.contains(it.id())).toList();
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
      return users.stream().filter(it -> it.id().equals(userId)).findFirst();
    }
  }
}
