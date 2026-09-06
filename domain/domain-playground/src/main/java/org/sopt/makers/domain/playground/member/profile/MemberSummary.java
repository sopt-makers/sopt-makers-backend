package org.sopt.makers.domain.playground.member.profile;

public record MemberSummary(
    Long id,
    String name,
    Integer generation,
    String profileImage,
    boolean hasProfile,
    boolean editActivitiesAble) {}
