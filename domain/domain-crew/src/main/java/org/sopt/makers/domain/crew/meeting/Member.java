package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEMBER_ROLE;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.LEADER_CANNOT_BE_CO_LEADER;

import java.util.Objects;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public record Member(Long meetingId, Long userId, MemberRole role) {

  public Member {
    if (meetingId == null || userId == null || role == null) {
      throw new MeetingException(INVALID_MEMBER_ROLE);
    }
  }

  public static Member leader(Long meetingId, Long userId) {
    return new Member(meetingId, userId, MemberRole.LEADER);
  }

  public static Member coLeader(Long meetingId, Long leaderUserId, Long userId) {
    if (Objects.equals(leaderUserId, userId)) {
      throw new MeetingException(LEADER_CANNOT_BE_CO_LEADER);
    }
    return new Member(meetingId, userId, MemberRole.CO_LEADER);
  }

  public static Member participant(Long meetingId, Long userId) {
    return new Member(meetingId, userId, MemberRole.PARTICIPANT);
  }

  public boolean matches(Long meetingId, Long userId, MemberRole role) {
    return Objects.equals(this.meetingId, meetingId)
        && Objects.equals(this.userId, userId)
        && this.role == role;
  }
}
