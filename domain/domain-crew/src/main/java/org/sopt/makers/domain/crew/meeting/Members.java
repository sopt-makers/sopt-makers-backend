package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.CO_LEADER_CANNOT_APPLY;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.FORBIDDEN_MEETING;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.LEADER_CANNOT_APPLY;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public record Members(List<Member> values) {

  public Members {
    values = values == null ? List.of() : List.copyOf(values);
  }

  public boolean hasRole(Long meetingId, Long userId, MemberRole role) {
    return values.stream().anyMatch(member -> member.matches(meetingId, userId, role));
  }

  public Optional<Member> findByRole(Long meetingId, MemberRole role) {
    return values.stream()
        .filter(member -> member.meetingId().equals(meetingId) && member.role() == role)
        .findFirst();
  }

  public List<Member> getByRole(MemberRole role) {
    return values.stream().filter(member -> member.role() == role).toList();
  }

  public long countByRole(Long meetingId, MemberRole role) {
    return values.stream()
        .filter(member -> member.meetingId().equals(meetingId) && member.role() == role)
        .count();
  }

  public void validateLeader(Long meetingId, Long userId) {
    if (!hasRole(meetingId, userId, MemberRole.LEADER)) {
      throw new MeetingException(FORBIDDEN_MEETING);
    }
  }

  public void validateManager(Long meetingId, Long userId) {
    if (!hasRole(meetingId, userId, MemberRole.LEADER)
        && !hasRole(meetingId, userId, MemberRole.CO_LEADER)) {
      throw new MeetingException(FORBIDDEN_MEETING);
    }
  }

  public void validateMember(Long meetingId, Long userId) {
    boolean isMember = values.stream().anyMatch(member -> member.belongsTo(meetingId, userId));
    if (!isMember) {
      throw new MeetingException(FORBIDDEN_MEETING);
    }
  }

  public void validateNotLeader(Long meetingId, Long userId) {
    if (hasRole(meetingId, userId, MemberRole.LEADER)) {
      throw new MeetingException(LEADER_CANNOT_APPLY);
    }
  }

  public void validateNotCoLeader(Long meetingId, Long userId) {
    if (hasRole(meetingId, userId, MemberRole.CO_LEADER)) {
      throw new MeetingException(CO_LEADER_CANNOT_APPLY);
    }
  }
}
