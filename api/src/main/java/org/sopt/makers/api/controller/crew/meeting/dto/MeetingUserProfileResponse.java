package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.user.Activity;

/** 기존 CREW 모임장 및 신청자 사용자 응답 계약. */
public record MeetingUserProfileResponse(
    Long id,
    String name,
    Long orgId,
    String profileImage,
    List<ActivityResponse> activities,
    String phone) {

  public static MeetingUserProfileResponse from(MeetingUser user) {
    if (user == null) {
      return null;
    }
    return new MeetingUserProfileResponse(
        user.id(),
        user.name(),
        user.id(),
        user.profileImage(),
        user.activities().stream().map(ActivityResponse::from).toList(),
        user.phone());
  }

  public record ActivityResponse(String part, int generation) {

    private static ActivityResponse from(Activity activity) {
      return new ActivityResponse(
          activity.part() == null ? null : activity.part().getName(), activity.generation());
    }
  }
}
