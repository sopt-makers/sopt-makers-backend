package org.sopt.makers.domain.user;

public record PokeUserProfile(
    Long userId, String name, String profileImage, Long generation, String part) {}
