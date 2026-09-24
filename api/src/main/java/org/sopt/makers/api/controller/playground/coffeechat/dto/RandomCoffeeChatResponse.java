package org.sopt.makers.api.controller.playground.coffeechat.dto;

import java.util.List;
import org.sopt.makers.domain.playground.coffeechat.service.CoffeeChatService.RandomCoffeeChatResult;

public record RandomCoffeeChatResponse(
    Long memberId,
    String coffeeChatBio,
    String profileImage,
    String name,
    String career,
    String organization,
    String companyJob,
    List<String> soptActivities,
    List<String> coffeeChatTopicType) {

  public static RandomCoffeeChatResponse from(RandomCoffeeChatResult result) {
    return new RandomCoffeeChatResponse(
        result.memberId(),
        result.coffeeChatBio(),
        result.profileImage(),
        result.name(),
        result.career(),
        result.organization(),
        result.companyJob(),
        result.soptActivities(),
        result.topicTypeList());
  }
}
