package org.sopt.makers.domain.crew.dining.model;

import static org.sopt.makers.domain.crew.dining.exception.DiningFailure.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.*;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.meeting.*;

@Getter
@Builder
@AllArgsConstructor
public class DiningMeeting {
  private Long id;
  private Long userId;
  private String title;
  private java.util.List<MeetingImage> imageURL;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime mStartDate;
  private LocalDateTime mEndDate;
  private MeetingJoinablePart[] joinableParts;
  private Integer createdGeneration;
  private LocalDateTime diningEndedAt;
  private boolean isDateModified;
  private boolean isJoinablePartsModified;
  private LocalDateTime createdTimestamp;

  public void updateDining(
      String title,
      List<MeetingImage> imageURL,
      LocalDateTime mStartDate,
      LocalDateTime mEndDate,
      MeetingJoinablePart[] joinableParts) {
    validateDiningNotEnded();

    if (!Objects.equals(this.mStartDate, mStartDate)) {
      this.isDateModified = true;
    }
    if (!toPartSet(this.joinableParts).equals(toPartSet(joinableParts))) {
      this.isJoinablePartsModified = true;
    }

    this.title = title;
    this.imageURL = imageURL;
    this.endDate = mEndDate;
    this.mStartDate = mStartDate;
    this.mEndDate = mEndDate;
    this.joinableParts = joinableParts;
  }

  private static Set<MeetingJoinablePart> toPartSet(MeetingJoinablePart[] parts) {
    if (parts == null) {
      return Set.of();
    }
    return Arrays.stream(parts).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  public void endDining(LocalDateTime now) {
    validateDiningNotEnded();
    this.diningEndedAt = now;
  }

  public boolean isDiningEnded() {
    return this.diningEndedAt != null;
  }

  public void validateDiningNotEnded() {
    if (isDiningEnded()) {
      throw new DiningException(ENDED_DINING);
    }
  }

  public LocalDateTime getmStartDate() {
    return mStartDate;
  }

  public LocalDateTime getmEndDate() {
    return mEndDate;
  }

  public boolean checkMeetingLeader(Long userId) {
    return Objects.equals(this.userId, userId);
  }

  public MeetingCategory getCategory() {
    return MeetingCategory.DINING;
  }

  public void validateMeetingCreator(Long userId) {
    if (!checkMeetingLeader(userId)) throw new DiningException(FORBIDDEN_EXCEPTION);
  }
}
