package org.sopt.makers.domain.app.home.facade;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.sopt.makers.domain.app.home.ActivityDurationCalculator;
import org.sopt.makers.domain.app.home.ActivityStatus;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.AppServiceEntryStatus;
import org.sopt.makers.domain.app.home.AppServiceName;
import org.sopt.makers.domain.app.home.FloatingButton;
import org.sopt.makers.domain.app.home.HomeAppServices;
import org.sopt.makers.domain.app.home.HtmlTagWrapper;
import org.sopt.makers.domain.app.home.MainView;
import org.sopt.makers.domain.app.home.ReviewForm;
import org.sopt.makers.domain.app.home.UserActiveInfo;
import org.sopt.makers.domain.app.home.service.AppServiceBadgeService;
import org.sopt.makers.domain.app.home.service.AppServiceService;
import org.sopt.makers.domain.app.notification.service.AppNotificationService;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPostRefreshEvent;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPostRefreshEvent;
import org.sopt.makers.domain.app.playground.service.PlaygroundPostCacheService;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.AppHomeUserPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class HomeFacade {

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");
  private static final int NO_SOPT_GENERATION = 0;
  private static final String PART_DELIMITER = "/";

  private final AppHomeUserPort appHomeUserPort;
  private final AppServiceService appServiceService;
  private final AppServiceBadgeService appServiceBadgeService;
  private final OperationConfigService operationConfigService;
  private final AppNotificationService appNotificationService;
  private final PlaygroundPostCacheService playgroundPostCacheService;
  private final SoptampMode soptampMode;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;
  private final Long currentGeneration;

  public HomeFacade(
      AppHomeUserPort appHomeUserPort,
      AppServiceService appServiceService,
      AppServiceBadgeService appServiceBadgeService,
      OperationConfigService operationConfigService,
      AppNotificationService appNotificationService,
      PlaygroundPostCacheService playgroundPostCacheService,
      SoptampMode soptampMode,
      ApplicationEventPublisher eventPublisher,
      Clock clock,
      @Value("${sopt.current.generation}") Long currentGeneration) {
    this.appHomeUserPort = appHomeUserPort;
    this.appServiceService = appServiceService;
    this.appServiceBadgeService = appServiceBadgeService;
    this.operationConfigService = operationConfigService;
    this.appNotificationService = appNotificationService;
    this.playgroundPostCacheService = playgroundPostCacheService;
    this.soptampMode = soptampMode;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
    this.currentGeneration = currentGeneration;
  }

  public String getHomeMainDescription(Long userId) {
    User user = appHomeUserPort.getWithActivities(userId);
    int duration =
        ActivityDurationCalculator.calculate(
            getMemberGenerationList(user), LocalDate.now(clock.withZone(KST)));
    return HtmlTagWrapper.wrapWithTag(user.profile().name(), "b")
        + "님은<br>SOPT와 "
        + duration
        + "개월째";
  }

  public HomeAppServices getHomeAppServices(Long userId) {
    List<AppService> appServices = appServiceService.getHomeAppServices();
    return new HomeAppServices(
        soptampMode.isAppjam(), checkAppServiceEntryStatus(userId, appServices));
  }

  public List<AppServiceEntryStatus> checkTabAppServiceEntryStatus(Long userId) {
    return checkAppServiceEntryStatus(userId, appServiceService.getTabAppServices());
  }

  public FloatingButton getFloatingButtonInfo(Long userId) {
    boolean isActive = isServiceActiveFor(userId, AppServiceName.FLOATING_BUTTON);
    Map<String, String> config =
        operationConfigService.getValuesByCategory(OperationConfigCategory.FLOATING_BUTTON);
    return new FloatingButton(
        config.get("imageUrl"),
        config.get("title"),
        config.get("expandedSubTitle"),
        config.get("collapsedSubtitle"),
        config.get("actionButtonName"),
        config.get("linkUrl"),
        isActive);
  }

  public ReviewForm getReviewFormInfo(Long userId) {
    boolean isActive = isServiceActiveFor(userId, AppServiceName.REVIEW_FORM);
    Map<String, String> config =
        operationConfigService.getValuesByCategory(OperationConfigCategory.REVIEW_FORM);
    return new ReviewForm(
        config.get("title"),
        config.get("subTitle"),
        config.get("actionButtonName"),
        config.get("linkUrl"),
        isActive);
  }

  public List<PlaygroundRecentPost> getPlaygroundRecentPosts() {
    return playgroundPostCacheService
        .getCachedRecentPosts()
        .orElseGet(
            () -> {
              eventPublisher.publishEvent(new PlaygroundRecentPostRefreshEvent());
              return List.of();
            });
  }

  public List<PlaygroundPopularPost> getPlaygroundPopularPosts() {
    return playgroundPostCacheService
        .getCachedPopularPosts()
        .orElseGet(
            () -> {
              eventPublisher.publishEvent(new PlaygroundPopularPostRefreshEvent());
              return List.of();
            });
  }

  public MainView getMainViewInfo(Long userId) {
    if (userId == null) {
      return MainView.unauthenticated();
    }
    User user = appHomeUserPort.getWithActivities(userId);
    return new MainView(
        getStatus(user),
        user.profile().name(),
        user.profile().profileImage(),
        getSoptActivityParts(user),
        getMemberGenerationList(user),
        appNotificationService.isAllRead(userId));
  }

  public UserActiveInfo getUserActiveInfo(Long userId) {
    return new UserActiveInfo(
        currentGeneration, getStatus(appHomeUserPort.getWithActivities(userId)));
  }

  private List<AppServiceEntryStatus> checkAppServiceEntryStatus(
      Long userId, List<AppService> appServices) {
    if (userId == null) {
      return appServices.stream().map(AppServiceEntryStatus::withoutBadge).toList();
    }
    ActivityStatus status = getStatus(appHomeUserPort.getWithActivities(userId));
    List<CompletableFuture<AppServiceEntryStatus>> futures =
        appServices.stream()
            .filter(appService -> appService.isVisibleTo(status))
            .map(
                appService ->
                    appServiceBadgeService.getAppServiceEntryStatusResponseAsync(
                        appService, userId))
            .toList();
    return futures.stream().map(CompletableFuture::join).toList();
  }

  private boolean isServiceActiveFor(Long userId, AppServiceName serviceName) {
    if (userId == null) {
      return false;
    }
    ActivityStatus status = getStatus(appHomeUserPort.getWithActivities(userId));
    AppService appService = appServiceService.getAppService(serviceName.getServiceName());
    return status == ActivityStatus.ACTIVE ? appService.activeUser() : appService.inactiveUser();
  }

  private ActivityStatus getStatus(User user) {
    long lastSoptGeneration =
        user.activities().activities().stream()
            .filter(Activity::isSopt)
            .mapToInt(Activity::generation)
            .max()
            .orElse(NO_SOPT_GENERATION);
    return lastSoptGeneration == currentGeneration
        ? ActivityStatus.ACTIVE
        : ActivityStatus.INACTIVE;
  }

  private String getSoptActivityParts(User user) {
    return user.activities().activities().stream()
        .filter(Activity::isSopt)
        .filter(activity -> activity.part() != null)
        .sorted(Comparator.comparingInt(Activity::generation))
        .map(activity -> activity.part().name())
        .distinct()
        .collect(Collectors.joining(PART_DELIMITER));
  }

  private List<Long> getMemberGenerationList(User user) {
    return user.activities().activities().stream()
        .map(activity -> (long) activity.generation())
        .distinct()
        .sorted(Comparator.reverseOrder())
        .toList();
  }
}
