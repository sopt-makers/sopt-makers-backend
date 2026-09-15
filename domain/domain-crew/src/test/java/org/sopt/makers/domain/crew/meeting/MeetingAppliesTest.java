package org.sopt.makers.domain.crew.meeting;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MeetingAppliesTest {

  @Test
  @DisplayName("승인된 신청만 PARTICIPANT 멤버로 분류한다")
  void resolvesApprovedAppliesAsParticipants() {
    MeetingApplies applies =
        new MeetingApplies(
            List.of(
                apply(1L, 10L, MeetingApplyStatus.APPROVE),
                apply(1L, 20L, MeetingApplyStatus.WAITING),
                apply(1L, 30L, MeetingApplyStatus.REJECT)));

    Members participants = applies.getParticipants(1L);

    assertThat(participants.values()).containsExactly(new Member(1L, 10L, MemberRole.PARTICIPANT));
    assertThat(applies.getApprovedCount(1L)).isEqualTo(1);
    assertThat(applies.isApproved(1L, 10L)).isTrue();
    assertThat(applies.isApproved(1L, 20L)).isFalse();
  }

  private MeetingApply apply(Long meetingId, Long userId, MeetingApplyStatus status) {
    return new MeetingApply(
        userId,
        MeetingApplyType.APPLY,
        meetingId,
        userId,
        "신청합니다.",
        LocalDateTime.of(2026, 9, 1, 12, 0),
        status,
        null,
        null);
  }
}
