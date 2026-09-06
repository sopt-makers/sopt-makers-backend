package org.sopt.makers.domain.playground.member.profile;

public record MemberInfo(
    MemberSummary summary,
    boolean hasCoffeeChat,
    boolean hasWorkPreference,
    boolean enableWorkPreferenceEvent) {}
