package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.FULL_MEETING_CAPACITY;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public record Meeting(
    Long id,
    Long meetingDemandId,
    String title,
    String subTitle,
    MeetingCategory category,
    List<MeetingImage> images,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer capacity,
    String description,
    String processDescription,
    LocalDateTime activityStartDate,
    LocalDateTime activityEndDate,
    String leaderDescription,
    String note,
    Boolean isMentorNeeded,
    Boolean canJoinOnlyActiveGeneration,
    MeetingJoinInfo joinInfo,
    Integer createdGeneration,
    Integer targetActiveGeneration,
    List<MeetingJoinablePart> joinableParts,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public Meeting {
    images = images == null ? List.of() : List.copyOf(images);
    joinableParts = joinableParts == null ? List.of() : List.copyOf(joinableParts);
  }

  public static Meeting create(
      Long meetingDemandId,
      String title,
      String subTitle,
      MeetingCategory category,
      List<MeetingImage> images,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer capacity,
      String description,
      String processDescription,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      String leaderDescription,
      String note,
      Boolean isMentorNeeded,
      Boolean canJoinOnlyActiveGeneration,
      MeetingJoinInfo joinInfo,
      Integer createdGeneration,
      Integer targetActiveGeneration,
      List<MeetingJoinablePart> joinableParts) {
    return new Meeting(
        null,
        meetingDemandId,
        title,
        subTitle,
        category,
        images,
        startDate,
        endDate,
        capacity,
        description,
        processDescription,
        activityStartDate,
        activityEndDate,
        leaderDescription,
        note,
        isMentorNeeded,
        canJoinOnlyActiveGeneration,
        joinInfo,
        createdGeneration,
        targetActiveGeneration,
        joinableParts,
        null,
        null);
  }

  public Meeting update(UpdateValues values) {
    return new Meeting(
        id,
        meetingDemandId,
        values.title(),
        values.subTitle(),
        values.category(),
        values.images(),
        values.startDate(),
        values.endDate(),
        values.capacity(),
        values.description(),
        values.processDescription(),
        values.activityStartDate(),
        values.activityEndDate(),
        values.leaderDescription(),
        values.note(),
        values.isMentorNeeded(),
        values.canJoinOnlyActiveGeneration(),
        values.joinInfo(),
        createdGeneration,
        values.targetActiveGeneration(),
        values.joinableParts(),
        createdAt,
        updatedAt);
  }

  public Meeting patch(UpdateValues values) {
    return new Meeting(
        id,
        meetingDemandId,
        values.title() == null ? title : values.title(),
        values.subTitle() == null ? subTitle : values.subTitle(),
        values.category() == null ? category : values.category(),
        values.images() == null ? images : values.images(),
        values.startDate() == null ? startDate : values.startDate(),
        values.endDate() == null ? endDate : values.endDate(),
        values.capacity() == null ? capacity : values.capacity(),
        values.description() == null ? description : values.description(),
        values.processDescription() == null ? processDescription : values.processDescription(),
        values.activityStartDate() == null ? activityStartDate : values.activityStartDate(),
        values.activityEndDate() == null ? activityEndDate : values.activityEndDate(),
        values.leaderDescription() == null ? leaderDescription : values.leaderDescription(),
        values.note() == null ? note : values.note(),
        values.isMentorNeeded() == null ? isMentorNeeded : values.isMentorNeeded(),
        values.canJoinOnlyActiveGeneration() == null
            ? canJoinOnlyActiveGeneration
            : values.canJoinOnlyActiveGeneration(),
        values.joinInfo() == null ? joinInfo : values.joinInfo(),
        createdGeneration,
        values.targetActiveGeneration() == null
            ? targetActiveGeneration
            : values.targetActiveGeneration(),
        values.joinableParts() == null ? joinableParts : values.joinableParts(),
        createdAt,
        updatedAt);
  }

  public MeetingStatus getMeetingStatus(LocalDateTime now) {
    if (now.isBefore(startDate)) {
      return MeetingStatus.BEFORE_START;
    }
    if (now.isBefore(endDate)) {
      return MeetingStatus.APPLY_ABLE;
    }
    return MeetingStatus.RECRUITMENT_COMPLETE;
  }

  public void validateCapacity(long approvedCount) {
    if (capacity != null && approvedCount >= capacity) {
      throw new MeetingException(FULL_MEETING_CAPACITY);
    }
  }

  public boolean isOnlyActiveGeneration() {
    return Boolean.TRUE.equals(canJoinOnlyActiveGeneration);
  }

  public record UpdateValues(
      String title,
      String subTitle,
      MeetingCategory category,
      List<MeetingImage> images,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer capacity,
      String description,
      String processDescription,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      String leaderDescription,
      String note,
      Boolean isMentorNeeded,
      Boolean canJoinOnlyActiveGeneration,
      MeetingJoinInfo joinInfo,
      Integer targetActiveGeneration,
      List<MeetingJoinablePart> joinableParts) {}
}
