package org.sopt.makers.domain.crew.flash;

import static org.sopt.makers.domain.crew.flash.exception.FlashFailure.FORBIDDEN_FLASH;
import static org.sopt.makers.domain.crew.flash.exception.FlashFailure.INVALID_FLASH_CAPACITY;
import static org.sopt.makers.domain.crew.flash.exception.FlashFailure.INVALID_FLASH_DATE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.sopt.makers.domain.crew.flash.exception.FlashException;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;

public record Flash(
    Long id,
    Long leaderUserId,
    Long meetingId,
    String title,
    String description,
    FlashTimingType timingType,
    LocalDateTime startDate,
    LocalDateTime endDate,
    LocalDateTime activityStartDate,
    LocalDateTime activityEndDate,
    FlashPlaceType placeType,
    String place,
    Integer minimumCapacity,
    Integer maximumCapacity,
    Integer createdGeneration,
    List<MeetingImage> images,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public Flash {
    images = images == null ? List.of() : List.copyOf(images);
    validateCapacity(minimumCapacity, maximumCapacity);
    validateActivityDates(activityStartDate, activityEndDate);
  }

  public static Flash create(
      Long leaderUserId, Long meetingId, UpdateValues values, Integer createdGeneration) {
    return new Flash(
        null,
        leaderUserId,
        meetingId,
        values.title(),
        values.description(),
        values.timingType(),
        values.startDate(),
        values.endDate(),
        values.activityStartDate(),
        values.activityEndDate(),
        values.placeType(),
        values.place(),
        values.minimumCapacity(),
        values.maximumCapacity(),
        createdGeneration,
        values.images(),
        null,
        null);
  }

  public Flash update(UpdateValues values) {
    return new Flash(
        id,
        leaderUserId,
        meetingId,
        values.title(),
        values.description(),
        values.timingType(),
        values.startDate(),
        values.endDate(),
        values.activityStartDate(),
        values.activityEndDate(),
        values.placeType(),
        values.place(),
        values.minimumCapacity(),
        values.maximumCapacity(),
        createdGeneration,
        values.images(),
        createdAt,
        updatedAt);
  }

  public void validateLeader(Long userId) {
    if (!Objects.equals(leaderUserId, userId)) {
      throw new FlashException(FORBIDDEN_FLASH);
    }
  }

  public MeetingStatus getStatus(LocalDateTime now) {
    if (now.isBefore(startDate)) {
      return MeetingStatus.BEFORE_START;
    }
    if (now.isBefore(endDate)) {
      return MeetingStatus.APPLY_ABLE;
    }
    return MeetingStatus.RECRUITMENT_COMPLETE;
  }

  private static void validateCapacity(Integer minimumCapacity, Integer maximumCapacity) {
    if (minimumCapacity == null
        || maximumCapacity == null
        || minimumCapacity < 1
        || maximumCapacity < minimumCapacity
        || maximumCapacity > 999) {
      throw new FlashException(INVALID_FLASH_CAPACITY);
    }
  }

  private static void validateActivityDates(
      LocalDateTime activityStartDate, LocalDateTime activityEndDate) {
    if (activityStartDate == null
        || activityEndDate == null
        || activityEndDate.isBefore(activityStartDate)) {
      throw new FlashException(INVALID_FLASH_DATE);
    }
  }

  public record UpdateValues(
      String title,
      String description,
      FlashTimingType timingType,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      FlashPlaceType placeType,
      String place,
      Integer minimumCapacity,
      Integer maximumCapacity,
      List<MeetingImage> images) {

    public UpdateValues {
      images = images == null ? List.of() : List.copyOf(images);
    }
  }
}
