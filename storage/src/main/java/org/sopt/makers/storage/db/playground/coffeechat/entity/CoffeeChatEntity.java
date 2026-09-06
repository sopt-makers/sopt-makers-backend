package org.sopt.makers.storage.db.playground.coffeechat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.CoffeeChat;
import org.sopt.makers.domain.playground.coffeechat.enums.Career;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatSection;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatTopicType;
import org.sopt.makers.domain.playground.coffeechat.enums.MeetingType;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.playground.coffeechat.converter.CoffeeChatSectionListConverter;
import org.sopt.makers.storage.db.playground.coffeechat.converter.CoffeeChatTopicTypeListConverter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coffee_chat")
public class CoffeeChatEntity extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "member_id")
  private Long memberId;

  @Column(name = "is_coffee_chat_activate")
  private Boolean isCoffeeChatActivate;

  @Enumerated(EnumType.STRING)
  @Column(name = "career")
  private Career career;

  @Column(name = "introduction", columnDefinition = "TEXT")
  private String introduction;

  @Convert(converter = CoffeeChatSectionListConverter.class)
  @Column(name = "section")
  private List<CoffeeChatSection> sections;

  @Column(name = "coffee_chat_bio", columnDefinition = "TEXT")
  private String coffeeChatBio;

  @Convert(converter = CoffeeChatTopicTypeListConverter.class)
  @Column(name = "coffee_chat_topic_type")
  private List<CoffeeChatTopicType> coffeeChatTopicTypes;

  @Column(name = "topic", columnDefinition = "TEXT")
  private String topic;

  @Enumerated(EnumType.STRING)
  @Column(name = "meeting_type")
  private MeetingType meetingType;

  @Column(name = "guideline", columnDefinition = "TEXT")
  private String guideline;

  private CoffeeChatEntity(
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
      String guideline) {
    this.id = id;
    this.memberId = memberId;
    this.isCoffeeChatActivate = isCoffeeChatActivate;
    this.career = career;
    this.introduction = introduction;
    this.sections = sections;
    this.coffeeChatBio = coffeeChatBio;
    this.coffeeChatTopicTypes = coffeeChatTopicTypes;
    this.topic = topic;
    this.meetingType = meetingType;
    this.guideline = guideline;
  }

  public static CoffeeChatEntity from(CoffeeChat coffeeChat) {
    return new CoffeeChatEntity(
        coffeeChat.id(),
        coffeeChat.memberId(),
        coffeeChat.isCoffeeChatActivate(),
        coffeeChat.career(),
        coffeeChat.introduction(),
        coffeeChat.sections(),
        coffeeChat.coffeeChatBio(),
        coffeeChat.coffeeChatTopicTypes(),
        coffeeChat.topic(),
        coffeeChat.meetingType(),
        coffeeChat.guideline());
  }

  public CoffeeChat toDomain() {
    return new CoffeeChat(
        id,
        memberId,
        isCoffeeChatActivate,
        career,
        introduction,
        sections,
        coffeeChatBio,
        coffeeChatTopicTypes,
        topic,
        meetingType,
        guideline,
        getCreatedAt(),
        getUpdatedAt());
  }
}
