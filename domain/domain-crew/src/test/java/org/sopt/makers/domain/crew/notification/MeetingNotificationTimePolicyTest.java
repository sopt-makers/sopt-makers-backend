package org.sopt.makers.domain.crew.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MeetingNotificationTimePolicyTest {

  private static final LocalDate DATE = LocalDate.of(2026, 8, 24);

  @Test
  @DisplayName("오전 8시부터 오후 10시까지 알림을 허용한다")
  void notificationAllowedDuringPublishableHours() {
    assertThat(MeetingNotificationTimePolicy.isPublishable(DATE.atTime(LocalTime.of(8, 0))))
        .isTrue();
    assertThat(MeetingNotificationTimePolicy.isPublishable(DATE.atTime(LocalTime.of(22, 0))))
        .isTrue();
  }

  @Test
  @DisplayName("허용 시간 밖에는 알림을 발송하지 않는다")
  void notificationRejectedOutsidePublishableHours() {
    assertThat(MeetingNotificationTimePolicy.isPublishable(DATE.atTime(LocalTime.of(7, 59))))
        .isFalse();
    assertThat(MeetingNotificationTimePolicy.isPublishable(DATE.atTime(LocalTime.of(22, 1))))
        .isFalse();
  }
}
