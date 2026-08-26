package org.sopt.makers.domain.app.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.notification.NotificationCategory.NEWS;
import static org.sopt.makers.domain.app.notification.NotificationCategory.NOTICE;
import static org.sopt.makers.domain.app.notification.NotificationType.SEND;
import static org.sopt.makers.domain.app.notification.NotificationType.SEND_ALL;
import static org.sopt.makers.domain.app.notification.exception.NotificationFailure.NOT_FOUND_NOTIFICATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.notification.Notification;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.notification.NotificationType;
import org.sopt.makers.domain.app.notification.RegisterNotificationCommand;
import org.sopt.makers.domain.app.notification.exception.NotificationException;
import org.sopt.makers.domain.app.notification.port.NotificationRepositoryPort;
import org.springframework.data.domain.Pageable;

@DisplayName("AppNotificationService 테스트")
class AppNotificationServiceTest {

  private InMemoryNotificationRepositoryPort notificationRepositoryPort;
  private AppNotificationService appNotificationService;

  @BeforeEach
  void setUp() {
    notificationRepositoryPort = new InMemoryNotificationRepositoryPort();
    appNotificationService =
        new AppNotificationService(notificationRepositoryPort, () -> List.of(1L, 2L, 3L));
  }

  @Test
  @DisplayName("SEND는 요청에 담긴 유저에게만 알림을 적재한다")
  void registersToRequestedUsersOnly() {
    appNotificationService.register(command(SEND, List.of(1L, 2L), "공지"));

    assertThat(notificationRepositoryPort.store).hasSize(2);
    assertThat(notificationRepositoryPort.store)
        .extracting(Notification::userId)
        .containsExactlyInAnyOrder(1L, 2L);
  }

  @Test
  @DisplayName("SEND_ALL은 전체 유저에게 알림을 적재한다")
  void registersToAllUsers() {
    appNotificationService.register(command(SEND_ALL, List.of(), "전체 공지"));

    assertThat(notificationRepositoryPort.store)
        .extracting(Notification::userId)
        .containsExactlyInAnyOrder(1L, 2L, 3L);
  }

  @Test
  @DisplayName("적재된 알림은 읽지 않은 상태로 시작한다")
  void registeredNotificationIsUnread() {
    appNotificationService.register(command(SEND, List.of(1L), "공지"));

    assertThat(appNotificationService.isAllRead(1L)).isFalse();
  }

  @Test
  @DisplayName("수신자가 없으면 아무것도 적재하지 않는다")
  void skipsWhenNoTarget() {
    appNotificationService.register(command(SEND, List.of(), "공지"));

    assertThat(notificationRepositoryPort.store).isEmpty();
  }

  @Test
  @DisplayName("카테고리를 주면 해당 카테고리만 조회한다")
  void filtersByCategory() {
    notificationRepositoryPort.add(notification(1L, "n1", NOTICE));
    notificationRepositoryPort.add(notification(1L, "n2", NEWS));

    assertThat(appNotificationService.getNotifications(1L, NEWS, 0, 10))
        .extracting(Notification::notificationId)
        .containsExactly("n2");
    assertThat(appNotificationService.getNotifications(1L, null, 0, 10)).hasSize(2);
  }

  @Test
  @DisplayName("notificationId가 없으면 유저의 알림을 모두 읽음 처리한다")
  void marksAllAsRead() {
    notificationRepositoryPort.add(notification(1L, "n1", NOTICE));
    notificationRepositoryPort.add(notification(1L, "n2", NOTICE));

    appNotificationService.markAsRead(1L, null);

    assertThat(appNotificationService.isAllRead(1L)).isTrue();
  }

  @Test
  @DisplayName("이미 읽은 알림을 다시 읽음 처리해도 예외가 발생하지 않는다")
  void markingReadNotificationAgainIsFine() {
    notificationRepositoryPort.add(notification(1L, "n1", NOTICE));
    appNotificationService.markAsRead(1L, "n1");

    appNotificationService.markAsRead(1L, "n1");

    assertThat(appNotificationService.isAllRead(1L)).isTrue();
  }

  @Test
  @DisplayName("다른 유저의 알림은 읽음 처리할 수 없다")
  void cannotReadOthersNotification() {
    notificationRepositoryPort.add(notification(2L, "n1", NOTICE));

    assertThatThrownBy(() -> appNotificationService.markAsRead(1L, "n1"))
        .isInstanceOf(NotificationException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_NOTIFICATION);
  }

  @Test
  @DisplayName("없는 알림을 조회하면 NOT_FOUND_NOTIFICATION 예외가 발생한다")
  void throwsWhenNotificationMissing() {
    assertThatThrownBy(() -> appNotificationService.getNotification(1L, "none"))
        .isInstanceOf(NotificationException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_NOTIFICATION);
  }

  private RegisterNotificationCommand command(
      NotificationType type, List<Long> userIds, String title) {
    return new RegisterNotificationCommand(
        userIds, "alarm-1", title, "내용", type, NOTICE, null, null);
  }

  private Notification notification(
      Long userId, String notificationId, NotificationCategory category) {
    return new Notification(
        null, userId, notificationId, "제목", "내용", SEND, category, null, null, false, null, null);
  }

  private static final class InMemoryNotificationRepositoryPort
      implements NotificationRepositoryPort {

    private final List<Notification> store = new ArrayList<>();
    private long sequence = 1L;

    void add(Notification notification) {
      saveAll(List.of(notification));
    }

    @Override
    public Optional<Notification> findByUserIdAndNotificationId(
        Long userId, String notificationId) {
      return store.stream()
          .filter(n -> n.userId().equals(userId) && n.notificationId().equals(notificationId))
          .findFirst();
    }

    @Override
    public List<Notification> findAllByUserId(Long userId, Pageable pageable) {
      return store.stream().filter(n -> n.userId().equals(userId)).toList();
    }

    @Override
    public List<Notification> findAllByUserIdAndCategory(
        Long userId, NotificationCategory category, Pageable pageable) {
      return store.stream()
          .filter(n -> n.userId().equals(userId) && n.category() == category)
          .toList();
    }

    @Override
    public void saveAll(List<Notification> notifications) {
      notifications.forEach(
          n ->
              store.add(
                  new Notification(
                      sequence++,
                      n.userId(),
                      n.notificationId(),
                      n.title(),
                      n.content(),
                      n.type(),
                      n.category(),
                      n.deepLink(),
                      n.webLink(),
                      n.isRead(),
                      null,
                      null)));
    }

    @Override
    public int markAsRead(Long userId, String notificationId) {
      Optional<Notification> found = findByUserIdAndNotificationId(userId, notificationId);
      found.ifPresent(this::replaceWithRead);
      return found.isPresent() ? 1 : 0;
    }

    @Override
    public void markAllAsRead(Long userId) {
      store.stream().filter(n -> n.userId().equals(userId)).toList().forEach(this::replaceWithRead);
    }

    @Override
    public boolean existsUnreadByUserId(Long userId) {
      return store.stream().anyMatch(n -> n.userId().equals(userId) && !n.isRead());
    }

    @Override
    public void deleteAllByUserId(Long userId) {
      store.removeIf(n -> n.userId().equals(userId));
    }

    private void replaceWithRead(Notification notification) {
      store.set(
          store.indexOf(notification),
          new Notification(
              notification.id(),
              notification.userId(),
              notification.notificationId(),
              notification.title(),
              notification.content(),
              notification.type(),
              notification.category(),
              notification.deepLink(),
              notification.webLink(),
              true,
              null,
              null));
    }
  }
}
