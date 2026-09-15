package org.sopt.makers.domain.playground.member.profile;

import java.util.List;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.WorkPreference;

public record WorkPreferenceRecommendationResult(
    boolean hasWorkPreference, List<WorkPreferenceRecommendedMember> recommendations) {

  public record WorkPreferenceRecommendedMember(
      Long id,
      String name,
      String profileImage,
      String university,
      Activity currentGenerationActivity,
      WorkPreference workPreference) {}
}
