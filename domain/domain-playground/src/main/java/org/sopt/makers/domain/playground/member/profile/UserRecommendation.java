package org.sopt.makers.domain.playground.member.profile;

public record UserRecommendation(
    Long id,
    String name,
    String profileImage,
    Integer generation,
    String part,
    RecommendationType type) {}
