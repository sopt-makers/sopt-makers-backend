package org.sopt.makers.api.controller.playground.coffeechat.dto;

import java.util.List;
import org.sopt.makers.domain.playground.coffeechat.service.CoffeeChatService.CoffeeChatDetailResult;

public record CoffeeChatDetailResponse(
    String coffeeChatBio,
    Long memberId,
    String profileImage,
    String name,
    String career,
    String organization,
    String companyJob,
    String phone,
    String email,
    String introduction,
    List<String> sections,
    List<String> coffeeChatTopicType,
    String topic,
    String meetingType,
    String guideline,
    Boolean isCoffeeChatActivate,
    Boolean isMine,
    Boolean isBlind) {

  public static CoffeeChatDetailResponse from(CoffeeChatDetailResult result) {
    return new CoffeeChatDetailResponse(
        result.bio(),
        result.memberId(),
        result.profileImage(),
        result.name(),
        result.career(),
        result.organization(),
        result.companyJob(),
        result.phone(),
        result.email(),
        result.introduction(),
        result.sections(),
        result.topicTypeList(),
        result.topic(),
        result.meetingType(),
        result.guideline(),
        result.isCoffeeChatActivate(),
        result.isMine(),
        result.isBlind());
  }
}
