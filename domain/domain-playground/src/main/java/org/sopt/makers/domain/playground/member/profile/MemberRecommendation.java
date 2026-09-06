package org.sopt.makers.domain.playground.member.profile;

public record MemberRecommendation(
    Long id, String name, String profileImage, Integer generation, String part, RecommendationType type) {}
