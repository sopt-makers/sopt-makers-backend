package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;

public record OnboardingProfileResponse(
    @Schema(description = "솝레터에서 쓰는 익명 닉네임", example = "그윽한 떡볶이") String nickname,
    @Schema(description = "온보딩을 마쳤는지 여부", example = "false") boolean isOnboarded,
    @Schema(description = "현재 기수", example = "35") int currentGeneration) {

  public static OnboardingProfileResponse of(SoptLetterProfile profile, int currentGeneration) {
    return new OnboardingProfileResponse(
        profile.nickname(), profile.isOnboarded(), currentGeneration);
  }
}
