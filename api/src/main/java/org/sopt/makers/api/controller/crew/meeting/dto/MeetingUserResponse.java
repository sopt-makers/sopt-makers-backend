package org.sopt.makers.api.controller.crew.meeting.dto;

import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.user.Activity;

public record MeetingUserResponse(
    Long userId, String name, String profileImage, Integer generation, String part) {

  public static MeetingUserResponse from(MeetingUser user) {
    if (user == null) {
      return null;
    }
    Activity activity = user.findLatestActivity().orElse(null);
    return new MeetingUserResponse(
        user.id(),
        user.name(),
        user.profileImage(),
        activity == null ? null : activity.generation(),
        activity == null || activity.part() == null ? null : activity.part().getName());
  }
}
