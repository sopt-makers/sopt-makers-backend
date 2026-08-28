package org.sopt.makers.domain.playground.coffeechat;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.coffeechat.enums.Career;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatSection;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatTopicType;
import org.sopt.makers.domain.playground.coffeechat.enums.MeetingType;

public record CoffeeChat(
    Long id,
    Long memberId,
    Boolean isCoffeeChatActivate,
    Career career,
    String introduction,
    List<CoffeeChatSection> sections,
    String coffeeChatBio,
    List<CoffeeChatTopicType> coffeeChatTopicTypes,
    String topic,
    MeetingType meetingType,
    String guideline,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public CoffeeChat withActive(boolean active) {
    return new CoffeeChat(
        id, memberId, active, career, introduction, sections, coffeeChatBio,
        coffeeChatTopicTypes, topic, meetingType, guideline, createdAt, updatedAt);
  }

  public CoffeeChat withInfo(
      Career career,
      String introduction,
      List<CoffeeChatSection> sections,
      String bio,
      List<CoffeeChatTopicType> topicTypes,
      String topic,
      MeetingType meetingType,
      String guideline) {
    return new CoffeeChat(
        id, memberId, isCoffeeChatActivate, career, introduction, sections, bio,
        topicTypes, topic, meetingType, guideline, createdAt, updatedAt);
  }
}
