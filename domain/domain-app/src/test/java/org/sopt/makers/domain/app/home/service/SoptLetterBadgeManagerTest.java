package org.sopt.makers.domain.app.home.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.SoptLetterColor;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterGenerator;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterService;
import org.sopt.makers.domain.app.soptletter.support.InMemoryOperationConfigPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterLikeNotificationRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterLikeRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterProfileRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterTopicReadRepositoryPort;

@DisplayName("SoptLetterBadgeManager 테스트")
class SoptLetterBadgeManagerTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 8, 24, 12, 0);
  private static final Long ME = 1L;
  private static final Long OTHER_PROFILE_ID = 99L;

  private InMemorySoptLetterTopicReadRepositoryPort readPort;
  private InMemorySoptLetterRepositoryPort letterPort;
  private SoptLetterBadgeManager soptLetterBadgeManager;

  @BeforeEach
  void setUp() {
    readPort = new InMemorySoptLetterTopicReadRepositoryPort();
    letterPort = new InMemorySoptLetterRepositoryPort(readPort);
    InMemorySoptLetterProfileRepositoryPort profilePort =
        new InMemorySoptLetterProfileRepositoryPort();
    profilePort.save(SoptLetterProfile.create(ME, "익명의 매운 떡볶이"));
    SoptLetterService soptLetterService =
        new SoptLetterService(
            letterPort,
            new InMemorySoptLetterLikeRepositoryPort(),
            new InMemorySoptLetterLikeNotificationRepositoryPort(),
            profilePort,
            new SoptLetterGenerator(),
            new OperationConfigService(new InMemoryOperationConfigPort()),
            Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC),
            event -> {});
    soptLetterBadgeManager = new SoptLetterBadgeManager(soptLetterService);
  }

  @Test
  @DisplayName("안 읽은 솝레터가 없으면 뱃지를 끈다")
  void disabledWhenNothingUnread() {
    assertThat(soptLetterBadgeManager.acquireAppServiceBadgeInfo(ME))
        .isEqualTo(AppServiceBadgeInfo.createWithAllDisabled());
  }

  @Test
  @DisplayName("모든 주제의 안 읽은 수를 더해 뱃지 문구로 준다")
  void sumsUnreadCountsOfAllTopics() {
    seed(100L, 3);
    seed(200L, 4);

    AppServiceBadgeInfo badge = soptLetterBadgeManager.acquireAppServiceBadgeInfo(ME);

    assertThat(badge.displayAlarmBadge()).isTrue();
    assertThat(badge.alarmBadge()).isEqualTo("7");
  }

  @Test
  @DisplayName("주제를 읽으면 그 주제의 안 읽은 수만큼 줄어든다")
  void decreasesAfterReadingTopic() {
    seed(100L, 3);
    Long lastLetterId = seed(200L, 4);

    readPort.upsertLastReadLetterId(ME, 200L, lastLetterId);

    assertThat(soptLetterBadgeManager.acquireAppServiceBadgeInfo(ME).alarmBadge()).isEqualTo("3");
  }

  @Test
  @DisplayName("99는 그대로, 100부터는 99+ 로 준다")
  void capsAtNinetyNinePlus() {
    seed(100L, 99);
    assertThat(soptLetterBadgeManager.acquireAppServiceBadgeInfo(ME).alarmBadge()).isEqualTo("99");

    seed(100L, 1);
    assertThat(soptLetterBadgeManager.acquireAppServiceBadgeInfo(ME).alarmBadge()).isEqualTo("99+");
  }

  private Long seed(Long topicId, int count) {
    Long lastId = null;
    for (int i = 0; i < count; i++) {
      lastId =
          letterPort
              .save(
                  new SoptLetter(
                      null,
                      OTHER_PROFILE_ID,
                      topicId,
                      0.0,
                      "글",
                      SoptLetterColor.BLUE_50,
                      null,
                      0,
                      NOW,
                      NOW))
              .id();
    }
    return lastId;
  }
}
