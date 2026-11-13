package org.sopt.makers.domain.crew.dining.service;

import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.dining.notification.DiningCloseGuideTarget;
import org.sopt.makers.domain.crew.dining.notification.DiningReminderTarget;

class DiningNotificationSchedulerTest {

  private final DiningReminderService reminderService = mock(DiningReminderService.class);
  private final DiningNotificationSender sender = mock(DiningNotificationSender.class);
  private final DiningNotificationScheduler scheduler =
      new DiningNotificationScheduler(reminderService, sender);

  @Test
  @DisplayName("마감 안내는 한 회식의 발송이 예외나 push 서버 실패로 끝나도 나머지 회식에 보낸다.")
  void closeGuide_continuesAfterFailure() {
    var throwing = new DiningCloseGuideTarget(1L, "예외 회식", 10L);
    var pushFailed = new DiningCloseGuideTarget(2L, "발송 실패 회식", 11L);
    var next = new DiningCloseGuideTarget(3L, "다음 회식", 12L);
    when(reminderService.findCloseGuideTargets()).thenReturn(List.of(throwing, pushFailed, next));
    when(sender.sendCloseGuideNotification(throwing)).thenThrow(new RuntimeException("unexpected"));
    when(sender.sendCloseGuideNotification(pushFailed)).thenReturn(false);
    when(sender.sendCloseGuideNotification(next)).thenReturn(true);

    scheduler.sendCloseGuideNotifications();

    verify(sender).sendCloseGuideNotification(pushFailed);
    verify(sender).sendCloseGuideNotification(next);
  }

  @Test
  @DisplayName("전날 리마인드는 한 회식의 발송이 예외나 push 서버 실패로 끝나도 나머지 회식에 보낸다.")
  void reminder_continuesAfterFailure() {
    var throwing = new DiningReminderTarget(1L, "예외 회식", List.of(10L));
    var pushFailed = new DiningReminderTarget(2L, "발송 실패 회식", List.of(11L));
    var next = new DiningReminderTarget(3L, "다음 회식", List.of(12L));
    when(reminderService.findReminderTargets()).thenReturn(List.of(throwing, pushFailed, next));
    when(sender.sendReminderNotification(throwing)).thenThrow(new RuntimeException("unexpected"));
    when(sender.sendReminderNotification(pushFailed)).thenReturn(false);
    when(sender.sendReminderNotification(next)).thenReturn(true);

    scheduler.sendReminderNotifications();

    verify(sender).sendReminderNotification(pushFailed);
    verify(sender).sendReminderNotification(next);
  }
}
