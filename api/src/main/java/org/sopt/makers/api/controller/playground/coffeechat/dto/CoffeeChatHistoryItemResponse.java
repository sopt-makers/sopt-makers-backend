package org.sopt.makers.api.controller.playground.coffeechat.dto;

import java.util.List;
import org.sopt.makers.domain.playground.coffeechat.service.CoffeeChatService.CoffeeChatHistoryResult;

public record CoffeeChatHistoryItemResponse(
    Long id,
    String coffeeChatBio,
    String name,
    String career,
    List<String> coffeeChatTopicType) {

  public static CoffeeChatHistoryItemResponse from(CoffeeChatHistoryResult result) {
    return new CoffeeChatHistoryItemResponse(
        result.id(),
        result.coffeeChatBio(),
        result.name(),
        result.career(),
        result.coffeeChatTopicType());
  }
}
