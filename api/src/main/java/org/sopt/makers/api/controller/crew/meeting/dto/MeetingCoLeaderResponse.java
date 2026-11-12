package org.sopt.makers.api.controller.crew.meeting.dto;

import org.sopt.makers.domain.crew.meeting.MeetingUser;

public record MeetingCoLeaderResponse(
    Long userId, Long orgId, String userName, String userprofileImage) {

  public static MeetingCoLeaderResponse from(MeetingUser user) {
    return new MeetingCoLeaderResponse(user.id(), user.id(), user.name(), user.profileImage());
  }
}
