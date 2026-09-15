package org.sopt.makers.domain.playground.community.member;

public record CommunityMemberSummary(
    Long id, String name, String profileImage, Activity activity, Career career) {

  public record Activity(int generation, String part, String team) {}

  public record Career(String companyName, String title) {}
}
