package org.sopt.makers.api.controller.playground.coffeechat.dto;

import java.util.List;
import org.sopt.makers.domain.playground.coffeechat.service.CoffeeChatService.CoffeeChatReviewResult;

public record CoffeeChatReviewInfoResponse(
    String profileImage,
    String nickname,
    List<String> soptActivities,
    List<String> coffeeChatTopicType,
    String content) {

  public static CoffeeChatReviewInfoResponse from(CoffeeChatReviewResult result) {
    return new CoffeeChatReviewInfoResponse(
        result.profileImage(),
        result.nickname(),
        result.soptActivities(),
        result.coffeeChatTopicType(),
        result.content());
  }
}
