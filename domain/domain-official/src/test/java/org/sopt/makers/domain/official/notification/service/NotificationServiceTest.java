package org.sopt.makers.domain.official.notification.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.notification.Notification;
import org.sopt.makers.domain.official.notification.exception.NotificationException;
import org.sopt.makers.domain.official.notification.port.NotificationRepositoryPort;

class NotificationServiceTest {
  private final NotificationRepositoryPort port = mock(NotificationRepositoryPort.class);
  private final NotificationService service = new NotificationService(port);

  @Test
  @DisplayName("중복되지 않은 이메일과 기수로 모집 알림을 신청한다")
  void registersNotification() {
    when(port.save(any())).thenReturn(new Notification(1L, "test@sopt.org", 39, null));
    assertThat(
            service
                .register(new NotificationService.RegisterNotificationCommand("test@sopt.org", 39))
                .id())
        .isEqualTo(1L);
    verify(port).existsByEmailAndGeneration("test@sopt.org", 39);
    verify(port).save(Notification.create("test@sopt.org", 39));
  }

  @Test
  @DisplayName("같은 이메일과 기수가 이미 있으면 추가 저장하지 않는다")
  void rejectsDuplicate() {
    when(port.existsByEmailAndGeneration("test@sopt.org", 39)).thenReturn(true);
    assertThatThrownBy(
            () ->
                service.register(
                    new NotificationService.RegisterNotificationCommand("test@sopt.org", 39)))
        .isInstanceOf(NotificationException.class);
    verify(port, never()).save(any());
  }

  @Test
  @DisplayName("같은 이메일도 다른 기수에는 알림을 신청할 수 있다")
  void allowsDifferentGeneration() {
    when(port.existsByEmailAndGeneration("test@sopt.org", 39)).thenReturn(true);
    when(port.save(any())).thenAnswer(inv -> inv.getArgument(0));
    assertThat(
            service
                .register(new NotificationService.RegisterNotificationCommand("test@sopt.org", 40))
                .generation())
        .isEqualTo(40);
  }
}
