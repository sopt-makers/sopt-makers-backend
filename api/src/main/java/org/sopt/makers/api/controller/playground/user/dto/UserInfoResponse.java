package org.sopt.makers.api.controller.playground.user.dto;

import org.sopt.makers.domain.playground.member.profile.UserInfo;

public record UserInfoResponse(
    Long id,
    String name,
    Integer generation,
    String profileImage,
    Boolean hasProfile,
    Boolean editActivitiesAble,
    Boolean hasCoffeeChat,
    Boolean hasWorkPreference,
    Boolean enableWorkPreferenceEvent) {

  public static UserInfoResponse from(UserInfo info) {
    return new UserInfoResponse(
        info.summary().id(),
        info.summary().name(),
        info.summary().generation(),
        info.summary().profileImage(),
        info.summary().hasProfile(),
        info.summary().editActivitiesAble(),
        info.hasCoffeeChat(),
        info.hasWorkPreference(),
        info.enableWorkPreferenceEvent());
  }
}
