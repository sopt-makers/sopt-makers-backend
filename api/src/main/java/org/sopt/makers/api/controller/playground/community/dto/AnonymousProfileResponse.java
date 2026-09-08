package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;

public record AnonymousProfileResponse(String nickname, String profileImgUrl) {

  public static AnonymousProfileResponse from(AnonymousProfile profile) {
    if (profile == null) {
      return null;
    }
    return new AnonymousProfileResponse(
        profile.nickname().nickname(), profile.profileImage().imageUrl());
  }
}
