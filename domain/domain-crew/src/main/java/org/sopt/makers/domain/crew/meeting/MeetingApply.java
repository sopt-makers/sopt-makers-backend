package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.ALREADY_PROCESSED_APPLY;

import java.time.LocalDateTime;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public record MeetingApply(
    Long id,
    MeetingApplyType type,
    Long meetingId,
    Long userId,
    String content,
    LocalDateTime appliedDate,
    MeetingApplyStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static MeetingApply createApply(
      Long meetingId, Long userId, String content, LocalDateTime appliedDate) {
    return new MeetingApply(
        null,
        MeetingApplyType.APPLY,
        meetingId,
        userId,
        content,
        appliedDate,
        MeetingApplyStatus.WAITING,
        null,
        null);
  }

  public MeetingApply updateStatus(MeetingApplyStatus updatedStatus) {
    validateDuplicateUpdateStatus(updatedStatus);
    return new MeetingApply(
        id, type, meetingId, userId, content, appliedDate, updatedStatus, createdAt, updatedAt);
  }

  public void validateDuplicateUpdateStatus(MeetingApplyStatus updatedStatus) {
    if (status == updatedStatus) {
      throw new MeetingException(ALREADY_PROCESSED_APPLY);
    }
  }

  public boolean isApproved() {
    return status == MeetingApplyStatus.APPROVE;
  }

  public boolean isParticipating() {
    return isApproved();
  }

  public Optional<Member> toParticipant() {
    if (!isApproved()) {
      return Optional.empty();
    }
    return Optional.of(Member.participant(meetingId, userId));
  }
}
