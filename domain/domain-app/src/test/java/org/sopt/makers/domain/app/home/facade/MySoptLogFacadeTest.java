package org.sopt.makers.domain.app.home.facade;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.fortune.FortuneWord;
import org.sopt.makers.domain.app.fortune.UserFortune;
import org.sopt.makers.domain.app.fortune.port.FortuneWordRepositoryPort;
import org.sopt.makers.domain.app.fortune.port.UserFortuneRepositoryPort;
import org.sopt.makers.domain.app.fortune.service.FortuneService;
import org.sopt.makers.domain.app.fortune.service.FortuneWordIdGenerator;
import org.sopt.makers.domain.app.home.MySoptLog;
import org.sopt.makers.domain.app.home.fake.FakeAppHomeUserPort;
import org.sopt.makers.domain.app.poke.fake.InMemoryFriendRepository;
import org.sopt.makers.domain.app.poke.fake.InMemoryPokeHistoryRepository;
import org.sopt.makers.domain.app.poke.service.AnonymousNameGenerator;
import org.sopt.makers.domain.app.poke.service.FriendService;
import org.sopt.makers.domain.app.poke.service.PokeService;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.sopt.makers.domain.app.soptamp.clap.service.ClapRegistrar;
import org.sopt.makers.domain.app.soptamp.clap.service.ClapService;
import org.sopt.makers.domain.app.soptamp.service.AppjamUserService;
import org.sopt.makers.domain.app.soptamp.service.StampService;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.support.InMemoryAppjamUserStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryClapStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryProfileSource;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryStampStore;
import org.sopt.makers.domain.app.soptamp.support.NoopStampFileStorage;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.User;

@DisplayName("MySoptLogFacade 테스트")
class MySoptLogFacadeTest {

  private static final long CURRENT_GENERATION = 38L;
  private static final LocalDate TODAY = LocalDate.of(2026, 9, 3);
  private static final Long ACTIVE_USER = 1L;
  private static final Long INACTIVE_USER = 2L;

  private FakeAppHomeUserPort userPort;
  private InMemoryAppjamUserStore appjamStore;
  private InMemoryUserFortuneRepositoryPort userFortunePort;
  private InMemoryPokeHistoryRepository pokeHistoryRepository;
  private InMemoryFriendRepository friendRepository;
  private InMemoryStampStore stampStore;
  private InMemoryClapStore clapStore;
  private MySoptLogFacade facade;

  @BeforeEach
  void setUp() {
    userPort = new FakeAppHomeUserPort();
    appjamStore = new InMemoryAppjamUserStore();
    userFortunePort = new InMemoryUserFortuneRepositoryPort();
    pokeHistoryRepository = new InMemoryPokeHistoryRepository();
    friendRepository = new InMemoryFriendRepository();
    stampStore = new InMemoryStampStore();
    clapStore = new InMemoryClapStore();

    userPort.add(userWith(ACTIVE_USER, List.of(activity(38, Part.SERVER))));
    userPort.add(userWith(INACTIVE_USER, List.of(activity(33, Part.DESIGN))));

    InMemoryFortuneWordRepositoryPort wordPort = new InMemoryFortuneWordRepositoryPort();
    wordPort.add(new FortuneWord(100L, "오늘은 코드가 잘 풀리는 날", 10L));
    Clock clock = Clock.fixed(Instant.parse("2026-09-03T03:00:00Z"), ZoneOffset.UTC);
    SoptampMode mode = new SoptampMode(false);
    facade =
        new MySoptLogFacade(
            userPort,
            new AppjamUserService(appjamStore),
            new FortuneService(
                (userId, date) -> Optional.empty(),
                wordPort,
                userFortunePort,
                new FortuneWordIdGenerator(wordPort),
                clock),
            new PokeService(pokeHistoryRepository, event -> {}),
            new FriendService(friendRepository, new AnonymousNameGenerator()),
            new StampService(stampStore, new NoopStampFileStorage()),
            new ClapService(
                new ClapRegistrar(clapStore, stampStore, mode, event -> {}),
                clapStore,
                stampStore,
                new InMemorySoptampUserStore(CURRENT_GENERATION),
                new InMemoryProfileSource()),
            mode,
            clock,
            CURRENT_GENERATION);
  }

  @Test
  @DisplayName("활동 회원은 솝탬프 집계와 콕찌르기 집계를 전부 채워 준다")
  void activeUserGetsFullLog() {
    userFortunePort.save(UserFortune.create(ACTIVE_USER, 100L, TODAY));
    pokeHistoryRepository.seed(ACTIVE_USER, 5L, false);
    pokeHistoryRepository.seed(ACTIVE_USER, 6L, false);
    friendRepository.seed(ACTIVE_USER, 5L, 3);
    friendRepository.seed(ACTIVE_USER, 6L, 12);
    stampStore.save(
        new Stamp(100L, "내용", List.of(), ACTIVE_USER, 10L, "2026-09-01", null, null, 4, 7, 0L));
    stampStore.save(
        new Stamp(101L, "내용", List.of(), ACTIVE_USER, 11L, "2026-09-02", null, null, 1, 3, 0L));
    clapStore.save(new Clap(null, 200L, ACTIVE_USER, 9, null));

    MySoptLog log = facade.getMySoptLog(ACTIVE_USER);

    assertThat(log.isActive()).isTrue();
    assertThat(log.isAppjamParticipant()).isFalse();
    assertThat(log.isFortuneChecked()).isTrue();
    assertThat(log.todayFortuneText()).isEqualTo("오늘은 코드가 잘 풀리는 날");
    assertThat(log.soptampCount()).isEqualTo(2);
    assertThat(log.viewCount()).isEqualTo(10);
    assertThat(log.myClapCount()).isEqualTo(5);
    assertThat(log.clapCount()).isEqualTo(9);
    assertThat(log.totalPokeCount()).isEqualTo(2);
    assertThat(log.newFriendsPokeCount()).isEqualTo(3);
    assertThat(log.bestFriendsPokeCount()).isZero();
    assertThat(log.soulmatesPokeCount()).isEqualTo(12);
  }

  @Test
  @DisplayName("비활동 회원이 앱잼에 참여하지 않으면 솝탬프 집계는 null 이고 운세 기본 문구를 준다")
  void inactiveNonAppjamUserGetsNulls() {
    MySoptLog log = facade.getMySoptLog(INACTIVE_USER);

    assertThat(log.isActive()).isFalse();
    assertThat(log.isAppjamParticipant()).isFalse();
    assertThat(log.isFortuneChecked()).isFalse();
    assertThat(log.todayFortuneText()).isEqualTo("오늘 내 운세는?");
    assertThat(log.soptampCount()).isNull();
    assertThat(log.viewCount()).isNull();
    assertThat(log.myClapCount()).isNull();
    assertThat(log.clapCount()).isNull();
    assertThat(log.totalPokeCount()).isZero();
  }

  @Test
  @DisplayName("비활동 회원이라도 앱잼 참여자면 솝탬프 집계를 준다")
  void inactiveAppjamParticipantGetsSoptampCounts() {
    appjamStore.save(new AppjamUser(1L, INACTIVE_USER, "팀", TeamNumber.FIRST));
    stampStore.save(
        new Stamp(100L, "내용", List.of(), INACTIVE_USER, 10L, "2026-09-01", null, null, 0, 2, 0L));

    MySoptLog log = facade.getMySoptLog(INACTIVE_USER);

    assertThat(log.isActive()).isFalse();
    assertThat(log.isAppjamParticipant()).isTrue();
    assertThat(log.soptampCount()).isEqualTo(1);
    assertThat(log.viewCount()).isEqualTo(2);
  }

  private static User userWith(Long id, List<Activity> activities) {
    Profile profile =
        Profile.of("홍길동", "user@sopt.org", "01012345678", LocalDate.of(2000, 1, 1), null);
    return User.createUser(id, null, profile, ActivityList.of(activities), false);
  }

  private static Activity activity(int generation, Part part) {
    return Activity.of((long) generation, generation, null, part, Role.MEMBER, true, null);
  }

  private static final class InMemoryUserFortuneRepositoryPort
      implements UserFortuneRepositoryPort {
    private final Map<Long, UserFortune> store = new HashMap<>();

    @Override
    public Optional<UserFortune> findByUserId(Long userId) {
      return Optional.ofNullable(store.get(userId));
    }

    @Override
    public UserFortune save(UserFortune userFortune) {
      store.put(userFortune.userId(), userFortune);
      return userFortune;
    }
  }

  private static final class InMemoryFortuneWordRepositoryPort
      implements FortuneWordRepositoryPort {
    private final Map<Long, FortuneWord> store = new HashMap<>();

    void add(FortuneWord word) {
      store.put(word.id(), word);
    }

    @Override
    public Optional<FortuneWord> findById(Long id) {
      return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Long> findAllIds() {
      return new ArrayList<>(store.keySet());
    }
  }
}
