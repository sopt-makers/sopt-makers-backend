package org.sopt.makers.domain.crew.meeting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

class MemberTest {

  @Test
  @DisplayName("모임 생성자는 LEADER 역할의 멤버가 된다")
  void createsLeader() {
    Member leader = Member.leader(1L, 10L);

    assertThat(leader).isEqualTo(new Member(1L, 10L, MemberRole.LEADER));
  }

  @Test
  @DisplayName("공동 모임장은 CO_LEADER 역할의 멤버가 된다")
  void createsCoLeader() {
    Member coLeader = Member.coLeader(1L, 10L, 20L);

    assertThat(coLeader).isEqualTo(new Member(1L, 20L, MemberRole.CO_LEADER));
  }

  @Test
  @DisplayName("모임장은 같은 모임의 공동 모임장이 될 수 없다")
  void leaderCannotBeCoLeader() {
    assertThatThrownBy(() -> Member.coLeader(1L, 10L, 10L)).isInstanceOf(MeetingException.class);
  }

  @Test
  @DisplayName("승인된 신청자는 PARTICIPANT 역할의 멤버가 된다")
  void approvedApplyBecomesParticipant() {
    MeetingApply approved = apply(1L, 20L, MeetingApplyStatus.APPROVE);

    assertThat(approved.toParticipant()).contains(new Member(1L, 20L, MemberRole.PARTICIPANT));
  }

  @Test
  @DisplayName("승인되지 않은 신청자는 멤버가 아니다")
  void unapprovedApplyDoesNotBecomeParticipant() {
    MeetingApply waiting = apply(1L, 20L, MeetingApplyStatus.WAITING);
    MeetingApply rejected = apply(1L, 30L, MeetingApplyStatus.REJECT);

    assertThat(waiting.toParticipant()).isEmpty();
    assertThat(rejected.toParticipant()).isEmpty();
    assertThat(waiting.isParticipating()).isFalse();
    assertThat(rejected.isParticipating()).isFalse();
  }

  private MeetingApply apply(Long meetingId, Long userId, MeetingApplyStatus status) {
    return new MeetingApply(
        1L,
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
