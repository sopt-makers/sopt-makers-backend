package org.sopt.makers.api.controller.playground.user.dto;

import java.util.List;
import org.sopt.makers.domain.playground.member.profile.CoffeeChatStatus;
import org.sopt.makers.domain.playground.member.profile.MemberProperties;

public record UserPropertiesResponse(
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
    Long uploadReviewCount) {

  public static UserPropertiesResponse from(MemberProperties properties) {
    return new UserPropertiesResponse(
        properties.id(),
        properties.major(),
        properties.job(),
        properties.organization(),
        properties.part(),
        properties.generation(),
        properties.coffeeChatStatus(),
        properties.receivedCoffeeChatCount(),
        properties.sentCoffeeChatCount(),
        properties.uploadSopticleCount(),
        properties.uploadReviewCount());
  }
}
