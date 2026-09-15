package org.sopt.makers.domain.app.home.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.home.exception.HomeFailure.NOT_FOUND_APP_SERVICE;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.home.ActivityStatus;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.sopt.makers.domain.app.home.AppServiceEntryStatus;
import org.sopt.makers.domain.app.home.FloatingButton;
import org.sopt.makers.domain.app.home.HomeAppServices;
import org.sopt.makers.domain.app.home.MainView;
import org.sopt.makers.domain.app.home.UserActiveInfo;
import org.sopt.makers.domain.app.home.exception.HomeException;
import org.sopt.makers.domain.app.home.fake.FakeAppHomeUserPort;
import org.sopt.makers.domain.app.home.fake.InMemoryAppServiceRepositoryPort;
import org.sopt.makers.domain.app.home.service.AppServiceBadgeManager;
import org.sopt.makers.domain.app.home.service.AppServiceBadgeService;
import org.sopt.makers.domain.app.home.service.AppServiceService;
import org.sopt.makers.domain.app.home.service.DefaultBadgeManager;
import org.sopt.makers.domain.app.notification.Notification;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.notification.port.NotificationRepositoryPort;
import org.sopt.makers.domain.app.notification.service.AppNotificationService;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.port.OperationConfigPort;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPostRefreshEvent;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPostRefreshEvent;
import org.sopt.makers.domain.app.playground.fake.InMemoryPlaygroundPostCacheRepositoryPort;
import org.sopt.makers.domain.app.playground.service.PlaygroundPostCacheService;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.User;
import org.springframework.data.domain.Pageable;

@DisplayName("HomeFacade 테스트")
class HomeFacadeTest {

  private static final Long CURRENT_GENERATION = 38L;
  private static final Long ACTIVE_USER = 1L;
  private static final Long INACTIVE_USER = 2L;

  private FakeAppHomeUserPort userPort;
  private InMemoryAppServiceRepositoryPort appServicePort;
  private InMemoryOperationConfigPort operationConfigPort;
  private InMemoryNotificationRepositoryPort notificationPort;
  private InMemoryPlaygroundPostCacheRepositoryPort postCachePort;
  private final List<Object> published = new ArrayList<>();
  private HomeFacade homeFacade;

  @BeforeEach
  void setUp() {
    userPort = new FakeAppHomeUserPort();
    appServicePort = new InMemoryAppServiceRepositoryPort();
    operationConfigPort = new InMemoryOperationConfigPort();
    notificationPort = new InMemoryNotificationRepositoryPort();
    postCachePort = new InMemoryPlaygroundPostCacheRepositoryPort();

    userPort.add(
        userWith(
            ACTIVE_USER,
            "홍길동",
            List.of(
                soptActivity(34, Part.WEB),
                soptActivity(38, Part.SERVER),
                makersActivity(36, Part.BACKEND))));
    userPort.add(userWith(INACTIVE_USER, "김솝트", List.of(soptActivity(33, Part.DESIGN))));

    Map<String, AppServiceBadgeManager> badgeManagers =
        Map.of(
            "defaultBadgeManager",
            new DefaultBadgeManager(),
            "pokeBadgeManager",
            userId -> AppServiceBadgeInfo.createWithEnabledDisplayAlarmBadge("3"));
    Clock clock = Clock.fixed(Instant.parse("2026-09-03T03:00:00Z"), ZoneOffset.UTC);
    homeFacade =
        new HomeFacade(
            userPort,
            new AppServiceService(appServicePort),
            new AppServiceBadgeService(badgeManagers),
            new OperationConfigService(operationConfigPort),
            new AppNotificationService(notificationPort, List::of),
            new PlaygroundPostCacheService(postCachePort),
            new SoptampMode(false),
            published::add,
            clock,
            CURRENT_GENERATION);
  }

  @Test
  @DisplayName("홈 설명은 이름을 b 태그로 감싸고 첫 기수부터 몇 개월째인지 붙인다")
  void buildsHomeDescription() {
    assertThat(homeFacade.getHomeMainDescription(ACTIVE_USER))
        .isEqualTo("<b>홍길동</b>님은<br>SOPT와 31개월째");
  }

  @Test
  @DisplayName("홈 서비스 응답은 앱잼 모드 플래그를 함께 준다")
  void homeAppServicesCarryAppjamMode() {
    addService("SOPT_LETTER", true, true);

    HomeAppServices result = homeFacade.getHomeAppServices(null);

    assertThat(result.isAppjamMode()).isFalse();
    assertThat(result.appServices())
        .extracting(AppServiceEntryStatus::serviceName)
        .containsExactly("솝레터");
  }

  @Test
  @DisplayName("로그인하지 않으면 서비스 전부를 뱃지 없이 준다")
  void anonymousGetsAllServicesWithoutBadge() {
    addService("POKE", true, false);
    addService("SOPTAMP", false, true);

    List<AppServiceEntryStatus> result = homeFacade.checkTabAppServiceEntryStatus(null);

    assertThat(result)
        .extracting(AppServiceEntryStatus::serviceName)
        .containsExactly("솝탬프", "콕찌르기");
    assertThat(result).extracting(AppServiceEntryStatus::displayAlarmBadge).containsOnly(false);
  }

  @Test
  @DisplayName("활동 회원에게는 activeUser 서비스만 보이고 POKE 뱃지가 붙는다")
  void activeUserSeesActiveServicesWithBadge() {
    addService("POKE", true, false);
    addService("SOPTAMP", false, true);

    List<AppServiceEntryStatus> result = homeFacade.checkTabAppServiceEntryStatus(ACTIVE_USER);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().serviceName()).isEqualTo("콕찌르기");
    assertThat(result.getFirst().displayAlarmBadge()).isTrue();
    assertThat(result.getFirst().alarmBadge()).isEqualTo("3");
  }

  @Test
  @DisplayName("비활동 회원에게는 inactiveUser 서비스만 보인다")
  void inactiveUserSeesInactiveServices() {
    addService("POKE", true, false);
    addService("SOPTAMP", false, true);

    List<AppServiceEntryStatus> result = homeFacade.checkTabAppServiceEntryStatus(INACTIVE_USER);

    assertThat(result).extracting(AppServiceEntryStatus::serviceName).containsExactly("솝탬프");
  }

  @Test
  @DisplayName("플로팅 버튼은 운영 설정 값과 유저 상태별 노출 여부를 준다")
  void buildsFloatingButton() {
    addService("FLOATING_BUTTON", true, false);
    operationConfigPort.add(OperationConfigCategory.FLOATING_BUTTON, "title", "제목");
    operationConfigPort.add(OperationConfigCategory.FLOATING_BUTTON, "linkUrl", "https://link");

    FloatingButton active = homeFacade.getFloatingButtonInfo(ACTIVE_USER);
    FloatingButton inactive = homeFacade.getFloatingButtonInfo(INACTIVE_USER);
    FloatingButton anonymous = homeFacade.getFloatingButtonInfo(null);

    assertThat(active.title()).isEqualTo("제목");
    assertThat(active.linkUrl()).isEqualTo("https://link");
    assertThat(active.imageUrl()).isNull();
    assertThat(active.isActive()).isTrue();
    assertThat(inactive.isActive()).isFalse();
    assertThat(anonymous.isActive()).isFalse();
  }

  @Test
  @DisplayName("플로팅 버튼 운영 설정이 없으면 필드가 null 인 채로 준다")
  void returnsNullFieldsWhenFloatingButtonConfigMissing() {
    FloatingButton result = homeFacade.getFloatingButtonInfo(null);

    assertThat(result.title()).isNull();
    assertThat(result.linkUrl()).isNull();
    assertThat(result.isActive()).isFalse();
  }

  @Test
  @DisplayName("플로팅 버튼 서비스 행이 없으면 NOT_FOUND_APP_SERVICE 예외가 발생한다")
  void throwsWhenFloatingButtonServiceMissing() {
    assertThatThrownBy(() -> homeFacade.getFloatingButtonInfo(ACTIVE_USER))
        .isInstanceOf(HomeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_APP_SERVICE);
  }

  @Test
  @DisplayName("게시글 캐시가 있으면 그대로 주고 이벤트를 발행하지 않는다")
  void returnsCachedPostsWithoutEvent() {
    PlaygroundRecentPost post =
        new PlaygroundRecentPost(
            1L,
            10L,
            "img",
            "홍길동",
            "38기 서버",
            "자유",
            "제목",
            "내용",
            "link",
            "2026-09-01 00:00:00.000000",
            false);
    postCachePort.setCachedRecentPosts(List.of(post));
    postCachePort.setCachedPopularPosts(List.of());

    assertThat(homeFacade.getPlaygroundRecentPosts()).containsExactly(post);
    assertThat(homeFacade.getPlaygroundPopularPosts()).isEmpty();
    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("게시글 캐시가 없으면 빈 목록을 주고 갱신 이벤트를 한 번 발행한다")
  void publishesRefreshEventOnCacheMiss() {
    assertThat(homeFacade.getPlaygroundRecentPosts()).isEmpty();
    assertThat(homeFacade.getPlaygroundPopularPosts()).isEmpty();

    assertThat(published)
        .containsExactly(
            new PlaygroundRecentPostRefreshEvent(), new PlaygroundPopularPostRefreshEvent());
  }

  @Test
  @DisplayName("로그인하지 않은 메인 뷰는 기본값이다")
  void anonymousMainView() {
    assertThat(homeFacade.getMainViewInfo(null)).isEqualTo(MainView.unauthenticated());
  }

  @Test
  @DisplayName("메인 뷰는 SOPT 파트를 기수순으로 잇고 전체 기수를 내림차순으로 준다")
  void buildsMainView() {
    notificationPort.addUnread(ACTIVE_USER);

    MainView view = homeFacade.getMainViewInfo(ACTIVE_USER);

    assertThat(view.status()).isEqualTo(ActivityStatus.ACTIVE);
    assertThat(view.name()).isEqualTo("홍길동");
    assertThat(view.profileImage()).isEqualTo("https://img/p.png");
    assertThat(view.part()).isEqualTo("WEB/SERVER");
    assertThat(view.generationList()).containsExactly(38L, 36L, 34L);
    assertThat(view.isAllConfirm()).isFalse();
  }

  @Test
  @DisplayName("마지막 SOPT 기수가 현재 기수가 아니면 INACTIVE 다")
  void inactiveWhenLastSoptGenerationIsNotCurrent() {
    UserActiveInfo info = homeFacade.getUserActiveInfo(INACTIVE_USER);

    assertThat(info.currentGeneration()).isEqualTo(CURRENT_GENERATION);
    assertThat(info.status()).isEqualTo(ActivityStatus.INACTIVE);
    assertThat(homeFacade.getMainViewInfo(INACTIVE_USER).isAllConfirm()).isTrue();
  }

  private void addService(String serviceName, boolean activeUser, boolean inactiveUser) {
    appServicePort.add(
        new AppService(
            (long) appServicePort.findAll().size() + 1,
            serviceName,
            activeUser,
            inactiveUser,
            "https://img/" + serviceName,
            "sopt://" + serviceName,
            LocalDateTime.of(2026, 9, appServicePort.findAll().size() + 1, 0, 0)));
  }

  private static User userWith(Long id, String name, List<Activity> activities) {
    Profile profile =
        Profile.of(
            name, "user@sopt.org", "01012345678", LocalDate.of(2000, 1, 1), "https://img/p.png");
    return User.createUser(id, null, profile, ActivityList.of(activities), false);
  }

  private static Activity soptActivity(int generation, Part part) {
    return Activity.of((long) generation, generation, null, part, Role.MEMBER, true, null);
  }

  private static Activity makersActivity(int generation, Part part) {
    return Activity.of((long) generation + 100, generation, null, part, Role.MEMBER, false, null);
  }

  private static final class InMemoryOperationConfigPort implements OperationConfigPort {
    private final List<OperationConfig> store = new ArrayList<>();

    void add(OperationConfigCategory category, String key, String value) {
      store.add(OperationConfig.text(category, key, value, "test"));
    }

    @Override
    public List<OperationConfig> findAllByCategory(OperationConfigCategory category) {
      return store.stream().filter(c -> c.category() == category).toList();
    }

    @Override
    public Optional<OperationConfig> findByCategoryAndKey(
        OperationConfigCategory category, String key) {
      return store.stream()
          .filter(c -> c.category() == category && c.key().equals(key))
          .findFirst();
    }

    @Override
    public OperationConfig save(OperationConfig operationConfig) {
      store.add(operationConfig);
      return operationConfig;
    }
  }

  private static final class InMemoryNotificationRepositoryPort
      implements NotificationRepositoryPort {
    private final List<Long> usersWithUnread = new ArrayList<>();

    void addUnread(Long userId) {
      usersWithUnread.add(userId);
    }

    @Override
    public boolean existsUnreadByUserId(Long userId) {
      return usersWithUnread.contains(userId);
    }

    @Override
    public Optional<Notification> findByUserIdAndNotificationId(
        Long userId, String notificationId) {
      return Optional.empty();
    }

    @Override
    public List<Notification> findAllByUserId(Long userId, Pageable pageable) {
      return List.of();
    }

    @Override
    public List<Notification> findAllByUserIdAndCategory(
        Long userId, NotificationCategory category, Pageable pageable) {
      return List.of();
    }

    @Override
    public void saveAll(List<Notification> notifications) {}

    @Override
    public int markAsRead(Long userId, String notificationId) {
      return 0;
    }

    @Override
    public void markAllAsRead(Long userId) {}

    @Override
    public void deleteAllByUserId(Long userId) {}
  }
}
