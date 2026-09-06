package org.sopt.makers.api.controller.app.soptamp.dto;

import org.sopt.makers.domain.app.soptamp.clap.ClapUserProfile;

public record ClapUserProfileResponse(
    String nickname, String profileImageUrl, String profileMessage, int clapCount) {

  public static ClapUserProfileResponse of(ClapUserProfile profile) {
    return new ClapUserProfileResponse(
        profile.nickname(),
        profile.profileImageUrl(),
        profile.profileMessage(),
        profile.clapCount());
  }
}
