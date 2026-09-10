package org.sopt.makers.domain.playground.member.profile;

public record UserInfo(
    UserSummary summary,
    boolean hasCoffeeChat,
    boolean hasWorkPreference,
    boolean enableWorkPreferenceEvent) {}
