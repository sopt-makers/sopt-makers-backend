package org.sopt.makers.api.controller.app.soptletter.dto;

import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;

public record OnboardingProfileResponse(
    String nickname, boolean isOnboarded, int currentGeneration) {

  public static OnboardingProfileResponse of(SoptLetterProfile profile, int currentGeneration) {
    return new OnboardingProfileResponse(
        profile.nickname(), profile.isOnboarded(), currentGeneration);
  }
}
