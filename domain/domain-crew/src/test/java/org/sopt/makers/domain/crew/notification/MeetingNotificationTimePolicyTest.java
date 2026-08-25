package org.sopt.makers.domain.crew.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class MeetingNotificationTimePolicyTest {

  private static final LocalDate DATE = LocalDate.of(2026, 8, 24);

  @Test
  void 오전_8시부터_오후_10시까지_알림을_허용한다() {
    assertThat(MeetingNotificationTimePolicy.isPublishable(DATE.atTime(LocalTime.of(8, 0))))
        .isTrue();
    assertThat(MeetingNotificationTimePolicy.isPublishable(DATE.atTime(LocalTime.of(22, 0))))
        .isTrue();
  }

  @Test
  void 허용_시간_밖에는_알림을_발송하지_않는다() {
    assertThat(MeetingNotificationTimePolicy.isPublishable(DATE.atTime(LocalTime.of(7, 59))))
        .isFalse();
    assertThat(MeetingNotificationTimePolicy.isPublishable(DATE.atTime(LocalTime.of(22, 1))))
        .isFalse();
  }
}
