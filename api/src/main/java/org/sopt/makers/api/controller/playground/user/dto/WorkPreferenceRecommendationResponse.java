package org.sopt.makers.api.controller.playground.user.dto;

import java.util.List;
import org.sopt.makers.domain.playground.member.profile.WorkPreferenceRecommendationResult;
import org.sopt.makers.domain.playground.member.profile.WorkPreferenceRecommendationResult.WorkPreferenceRecommendedMember;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.WorkPreference;

public record WorkPreferenceRecommendationResponse(
    boolean hasWorkPreference, List<RecommendedMember> recommendations) {

  public record RecommendedMember(
      Long id,
      String name,
      String profileImage,
      String university,
      UserProfileResponse.MemberSoptActivityResponse activity,
      WorkPreferenceResponse.WorkPreferenceData workPreference) {}

  public static WorkPreferenceRecommendationResponse from(WorkPreferenceRecommendationResult result) {
    List<RecommendedMember> members =
        result.recommendations().stream()
            .map(WorkPreferenceRecommendationResponse::toRecommendedMember)
            .toList();
    return new WorkPreferenceRecommendationResponse(result.hasWorkPreference(), members);
  }

  private static RecommendedMember toRecommendedMember(WorkPreferenceRecommendedMember member) {
    Activity activity = member.currentGenerationActivity();
    UserProfileResponse.MemberSoptActivityResponse activityResponse =
        activity == null
            ? null
            : new UserProfileResponse.MemberSoptActivityResponse(
                activity.id(),
                activity.generation(),
                activity.part() == null ? null : activity.part().getName(),
                activity.team() == null ? null : activity.team().getDisplayName());

    WorkPreference workPreference = member.workPreference();
    WorkPreferenceResponse.WorkPreferenceData workPreferenceData =
        workPreference == null
            ? null
            : new WorkPreferenceResponse.WorkPreferenceData(
                workPreference.ideationStyle() == null ? null : workPreference.ideationStyle().getValue(),
                workPreference.workTime() == null ? null : workPreference.workTime().getValue(),
                workPreference.communicationStyle() == null
                    ? null
                    : workPreference.communicationStyle().getValue(),
                workPreference.workPlace() == null ? null : workPreference.workPlace().getValue(),
                workPreference.feedbackStyle() == null ? null : workPreference.feedbackStyle().getValue());

    return new RecommendedMember(
        member.id(), member.name(), member.profileImage(), member.university(), activityResponse, workPreferenceData);
  }
}
