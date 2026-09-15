package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;

public record MemberResponse(
    Long id, String name, String profileImage, ActivityResponse activity, CareerResponse careers) {

  public static MemberResponse from(CommunityMemberSummary summary) {
    if (summary == null) {
      return null;
    }
    return new MemberResponse(
        summary.id(),
        summary.name(),
        summary.profileImage(),
        ActivityResponse.from(summary.activity()),
        CareerResponse.from(summary.career()));
  }

  public record ActivityResponse(int generation, String part, String team) {
    public static ActivityResponse from(CommunityMemberSummary.Activity activity) {
      return activity == null
          ? null
          : new ActivityResponse(activity.generation(), activity.part(), activity.team());
    }
  }

  public record CareerResponse(String companyName, String title) {
    public static CareerResponse from(CommunityMemberSummary.Career career) {
      return career == null ? null : new CareerResponse(career.companyName(), career.title());
    }
  }
}
