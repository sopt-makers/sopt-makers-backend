package org.sopt.makers.api.controller.playground.member.dto;

import org.sopt.makers.domain.playground.member.profile.MemberInfo;

public record MemberInfoResponse(
    Long id,
    String name,
    Integer generation,
    String profileImage,
    Boolean hasProfile,
    Boolean editActivitiesAble,
    Boolean hasCoffeeChat,
    Boolean hasWorkPreference,
    Boolean enableWorkPreferenceEvent) {

  public static MemberInfoResponse from(MemberInfo info) {
    return new MemberInfoResponse(
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
