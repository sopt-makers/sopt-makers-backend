package org.sopt.makers.domain.playground.member.profile;

import java.util.List;

public record MemberProperties(
    Long id,
    String major,
    String job,
    String organization,
    List<String> part,
    List<Integer> generation,
    CoffeeChatStatus coffeeChatStatus,
    Long receivedCoffeeChatCount,
    Long sentCoffeeChatCount,
    Long uploadSopticleCount,
    Long uploadReviewCount) {}
