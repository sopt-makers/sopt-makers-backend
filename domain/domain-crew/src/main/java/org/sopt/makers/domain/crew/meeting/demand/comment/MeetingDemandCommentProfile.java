package org.sopt.makers.domain.crew.meeting.demand.comment;

import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandAnonymousProfile;

public record MeetingDemandCommentProfile(
    Long id,
    Long meetingDemandId,
    Long userId,
    String anonymousNickname,
    Integer anonymousImageNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static MeetingDemandCommentProfile create(
      Long meetingDemandId, Long userId, String anonymousNickname, Integer anonymousImageNumber) {
    return new MeetingDemandCommentProfile(
        null,
        meetingDemandId,
        userId,
        anonymousNickname == null
            ? MeetingDemandAnonymousProfile.generateNickname()
            : anonymousNickname,
        anonymousImageNumber == null
            ? MeetingDemandAnonymousProfile.generateImageNumber()
            : anonymousImageNumber,
        null,
        null);
  }

  public String anonymousImageUrl() {
    return MeetingDemandAnonymousProfile.getImageUrl(anonymousImageNumber);
  }
}
