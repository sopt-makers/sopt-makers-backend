package org.sopt.makers.api.controller.playground.coffeechat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.sopt.makers.domain.playground.coffeechat.enums.Career;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatSection;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatTopicType;
import org.sopt.makers.domain.playground.coffeechat.enums.MeetingType;

public record CoffeeChatDetailsRequest(
    @NotNull @Valid MemberInfoRequest memberInfo,
    @NotNull @Valid CoffeeChatInfo coffeeChatInfo) {

  public record MemberInfoRequest(Career career, String introduction) {}

  public record CoffeeChatInfo(
      List<CoffeeChatSection> sections,
      String bio,
      List<CoffeeChatTopicType> topicTypes,
      String topic,
      MeetingType meetingType,
      String guideline) {}
}
