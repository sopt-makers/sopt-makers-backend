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
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.soptamp.support.FakePushSender;
import org.sopt.makers.domain.app.soptletter.SoptLetterLikeEvent;
import org.sopt.makers.domain.app.soptletter.support.InMemoryAppSoptLetterUserPort;
import org.sopt.makers.domain.app.soptletter.support.InMemoryOperationConfigPort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;

@DisplayName("SoptLetterLikeAlarmSender 테스트")
class SoptLetterLikeAlarmSenderTest {

  private static final int CURRENT_GENERATION = 37;
  private static final Long AUTHOR = 1L;
  private static final Long LIKER = 2L;

  private FakePushSender pushSender;
  private InMemoryOperationConfigPort operationConfigPort;
  private InMemoryAppSoptLetterUserPort userPort;
  private SoptLetterLikeAlarmSender alarmSender;

  @BeforeEach
  void setUp() {
    pushSender = new FakePushSender();
    operationConfigPort = new InMemoryOperationConfigPort();
    userPort = new InMemoryAppSoptLetterUserPort();
    alarmSender =
        new SoptLetterLikeAlarmSender(
            pushSender,
            new OperationConfigService(operationConfigPort),
            userPort,
            CURRENT_GENERATION);
  }

  @Test
  @DisplayName("글쓴이가 현재 기수 활동 회원이면 운영 설정 문구로 좋아요 알림을 보낸다")
  void sendsToActiveAuthor() {
    saveConfig("내 솝레터에 좋아요가 달렸어요", "누가 내 솝레터를 좋아했는지 확인해 보세요");
    userPort.add(user(AUTHOR, List.of(soptActivity(36), soptActivity(37))));

    alarmSender.handle(new SoptLetterLikeEvent(LIKER, AUTHOR, 100L, 5L));

    assertThat(pushSender.sent())
        .containsExactly(
            new PushMessage(
                Set.of(AUTHOR),
                "내 솝레터에 좋아요가 달렸어요",
                "누가 내 솝레터를 좋아했는지 확인해 보세요",
                NotificationCategory.NOTICE,
                "soptletter?topicId=100&messageId=5&pushType=soptletter_liked",
                null));
  }

  @Test
  @DisplayName("글쓴이의 마지막 SOPT 활동이 현재 기수가 아니면 보내지 않는다")
  void skipsInactiveAuthor() {
    saveConfig("내 솝레터에 좋아요가 달렸어요", "누가 내 솝레터를 좋아했는지 확인해 보세요");
    userPort.add(user(AUTHOR, List.of(soptActivity(36))));

    alarmSender.handle(new SoptLetterLikeEvent(LIKER, AUTHOR, 100L, 5L));

    assertThat(pushSender.sent()).isEmpty();
  }

  @Test
  @DisplayName("글쓴이 유저를 찾지 못하면 보내지 않는다")
  void skipsMissingAuthor() {
    saveConfig("내 솝레터에 좋아요가 달렸어요", "누가 내 솝레터를 좋아했는지 확인해 보세요");
    alarmSender.handle(new SoptLetterLikeEvent(LIKER, AUTHOR, 100L, 5L));

    assertThat(pushSender.sent()).isEmpty();
  }

  @Test
  @DisplayName("알림 문구 설정이 비어 있으면 보내지 않는다")
  void skipsWhenConfigBlank() {
    saveConfig(" ", "누가 내 솝레터를 좋아했는지 확인해 보세요");
    userPort.add(user(AUTHOR, List.of(soptActivity(37))));

    alarmSender.handle(new SoptLetterLikeEvent(LIKER, AUTHOR, 100L, 5L));

    assertThat(pushSender.sent()).isEmpty();
  }

  private void saveConfig(String title, String content) {
    operationConfigPort.save(
        OperationConfig.text(
            OperationConfigCategory.SOPT_LETTER, "likeNotificationTitle", title, "제목"));
    operationConfigPort.save(
        OperationConfig.text(
            OperationConfigCategory.SOPT_LETTER, "likeNotificationContent", content, "내용"));
  }

  private static User user(Long id, List<Activity> activities) {
    Profile profile =
        Profile.of("김솝트", "user@sopt.org", "01012345678", LocalDate.of(2000, 1, 1), null);
    return User.createUser(id, null, profile, ActivityList.of(activities), false);
  }
}
