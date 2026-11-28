package org.sopt.makers.domain.app.soptletter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sopt.makers.domain.app.soptletter.support.InMemoryAppSoptLetterUserPort.soptActivity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.soptamp.support.FakePushSender;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.support.InMemoryAppSoptLetterUserPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterProfileRepositoryPort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;

@DisplayName("SoptLetterNotificationService 테스트")
class SoptLetterNotificationServiceTest {

  private static final int CURRENT_GENERATION = 37;
  private static final String DEEP_LINK = "soptletter";

  private FakePushSender pushSender;
  private InMemoryAppSoptLetterUserPort userPort;
  private InMemorySoptLetterProfileRepositoryPort profilePort;
  private SoptLetterNotificationService notificationService;

  @BeforeEach
  void setUp() {
    pushSender = new FakePushSender();
    userPort = new InMemoryAppSoptLetterUserPort();
    profilePort = new InMemorySoptLetterProfileRepositoryPort();
    notificationService =
        new SoptLetterNotificationService(userPort, profilePort, pushSender, CURRENT_GENERATION);
  }

  @Test
  @DisplayName("미작성 리마인드는 현재 기수 회원 중 솝레터를 한 번도 쓰지 않은 사람에게만 보낸다")
  void remindsOnlyUnwrittenCurrentMembers() {
    userPort.add(user(1L, List.of(soptActivity(37))));
    userPort.add(user(2L, List.of(soptActivity(36), soptActivity(37))));
    userPort.add(user(3L, List.of(soptActivity(37))));
    userPort.add(user(4L, List.of(soptActivity(36))));
    Long writer = profilePort.save(SoptLetterProfile.create(2L, "익명의 매운 떡볶이")).id();
    profilePort.markWritten(writer);
    profilePort.save(SoptLetterProfile.create(3L, "익명의 시원한 냉면"));

    notificationService.sendUnwrittenReminder("솝레터를 써 보세요", "아직 솝레터를 안 썼어요", DEEP_LINK);

    assertThat(pushSender.sent())
        .containsExactly(
            new PushMessage(
                Set.of(1L, 3L),
                "솝레터를 써 보세요",
                "아직 솝레터를 안 썼어요",
                NotificationCategory.NOTICE,
                DEEP_LINK,
                null));
  }

  @Test
  @DisplayName("현재 기수 회원이 모두 솝레터를 썼으면 리마인드를 보내지 않는다")
  void skipsReminderWhenEveryoneWrote() {
    userPort.add(user(1L, List.of(soptActivity(37))));
    profilePort.markWritten(profilePort.save(SoptLetterProfile.create(1L, "익명의 매운 떡볶이")).id());

    notificationService.sendUnwrittenReminder("제목", "내용", DEEP_LINK);

    assertThat(pushSender.sent()).isEmpty();
  }

  @Test
  @DisplayName("새 주제 알림은 현재 기수 회원 전체에게 보낸다")
  void sendsNewTopicToAllCurrentMembers() {
    userPort.add(user(1L, List.of(soptActivity(37))));
    userPort.add(user(2L, List.of(soptActivity(37))));
    userPort.add(user(3L, List.of(soptActivity(36))));
    profilePort.markWritten(profilePort.save(SoptLetterProfile.create(2L, "익명의 매운 떡볶이")).id());

    notificationService.sendNewTopic("새 주제가 열렸어요", "앱잼 회고를 남겨 보세요", DEEP_LINK);

    assertThat(pushSender.sent())
        .containsExactly(
            new PushMessage(
                Set.of(1L, 2L),
                "새 주제가 열렸어요",
                "앱잼 회고를 남겨 보세요",
                NotificationCategory.NOTICE,
                DEEP_LINK,
                null));
  }

  @Test
  @DisplayName("현재 기수 회원이 없으면 새 주제 알림을 보내지 않는다")
  void skipsNewTopicWhenNoCurrentMembers() {
    userPort.add(user(1L, List.of(soptActivity(36))));

    notificationService.sendNewTopic("제목", "내용", DEEP_LINK);

    assertThat(pushSender.sent()).isEmpty();
  }

  private static User user(Long id, List<Activity> activities) {
    Profile profile =
        Profile.of("김솝트", "user@sopt.org", "01012345678", LocalDate.of(2000, 1, 1), null);
    return User.createUser(id, null, profile, ActivityList.of(activities), false);
  }
}
