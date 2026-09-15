package org.sopt.makers.domain.playground.member.profile;

public record SameGenerationAndPartUser(
    Long id, String name, String profileImage, Integer generation, String part) {}
