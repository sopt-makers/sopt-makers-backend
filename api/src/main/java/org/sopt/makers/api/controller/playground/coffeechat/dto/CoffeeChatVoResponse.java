package org.sopt.makers.api.controller.playground.coffeechat.dto;

import java.util.List;
import org.sopt.makers.domain.playground.coffeechat.service.CoffeeChatService.CoffeeChatVoResult;

public record CoffeeChatVoResponse(
    Long memberId,
    String coffeeChatBio,
    List<String> coffeeChatTopicType,
    String profileImage,
    String name,
    String career,
    String organization,
    String companyJob,
    List<String> soptActivities,
    Boolean isMine,
    Boolean isBlind) {

  public static CoffeeChatVoResponse from(CoffeeChatVoResult result) {
    return new CoffeeChatVoResponse(
        result.memberId(),
        result.bio(),
        result.topicTypeList(),
        result.profileImage(),
        result.name(),
        result.career(),
        result.organization(),
        result.companyJob(),
        result.soptActivities(),
        result.isMine(),
        result.isBlind());
  }
}
