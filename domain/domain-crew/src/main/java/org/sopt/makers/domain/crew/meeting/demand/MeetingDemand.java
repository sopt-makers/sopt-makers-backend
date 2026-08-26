package org.sopt.makers.domain.crew.meeting.demand;

import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.FORBIDDEN_MEETING_DEMAND;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.WRITER_CANNOT_WAIT_MEETING_DEMAND;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandException;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

public record MeetingDemand(
    Long id,
    Long userId,
    String shortIntro,
    String expectation,
    MeetingDemandStatus status,
    String anonymousNickname,
    Integer anonymousImageNumber,
    List<MeetingKeywordType> meetingKeywordTypes,
    MeetingDemandJoinInfo joinInfo,
    int waitCount,
    int commentCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public MeetingDemand {
    meetingKeywordTypes =
        meetingKeywordTypes == null ? List.of() : List.copyOf(meetingKeywordTypes);
  }

  public static MeetingDemand create(
      Long userId,
      String shortIntro,
      String expectation,
      List<MeetingKeywordType> meetingKeywordTypes,
      MeetingDemandJoinInfo joinInfo) {
    return new MeetingDemand(
        null,
        userId,
        shortIntro,
        expectation,
        MeetingDemandStatus.BEFORE_OPEN,
        MeetingDemandAnonymousProfile.generateNickname(),
        MeetingDemandAnonymousProfile.generateImageNumber(),
        meetingKeywordTypes,
        joinInfo,
        0,
        0,
        null,
        null);
  }

  public MeetingDemand open() {
    if (status == MeetingDemandStatus.OPENED) {
      return this;
    }
    return copyWith(MeetingDemandStatus.OPENED, waitCount, commentCount);
  }

  public MeetingDemand syncWaitCount(long count) {
    return copyWith(status, Math.toIntExact(count), commentCount);
  }

  public MeetingDemand increaseCommentCount() {
    return copyWith(status, waitCount, commentCount + 1);
  }

  public MeetingDemand decreaseCommentCount() {
    return copyWith(status, waitCount, Math.max(0, commentCount - 1));
  }

  public boolean isWriter(Long requestUserId) {
    return Objects.equals(userId, requestUserId);
  }

  public void validateWriter(Long requestUserId) {
    if (!isWriter(requestUserId)) {
      throw new MeetingDemandException(FORBIDDEN_MEETING_DEMAND);
    }
  }

  public void validateNotWriter(Long requestUserId) {
    if (isWriter(requestUserId)) {
      throw new MeetingDemandException(WRITER_CANNOT_WAIT_MEETING_DEMAND);
    }
  }

  public String anonymousImageUrl() {
    return MeetingDemandAnonymousProfile.getImageUrl(anonymousImageNumber);
  }

  private MeetingDemand copyWith(
      MeetingDemandStatus nextStatus, int nextWaitCount, int nextCommentCount) {
    return new MeetingDemand(
        id,
        userId,
        shortIntro,
        expectation,
        nextStatus,
        anonymousNickname,
        anonymousImageNumber,
        meetingKeywordTypes,
        joinInfo,
        nextWaitCount,
        nextCommentCount,
        createdAt,
        updatedAt);
  }
}
