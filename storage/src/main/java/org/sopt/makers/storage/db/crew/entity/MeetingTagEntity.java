package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTag;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTagType;
import org.sopt.makers.domain.crew.meeting.tag.WelcomeMessageType;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.crew.converter.MeetingKeywordTypeListConverter;
import org.sopt.makers.storage.db.crew.converter.WelcomeMessageTypeListConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "tag")
public class MeetingTagEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "tag_type", nullable = false, length = 20)
  private MeetingTagType type;

  @Column(name = "meeting_id", nullable = false, unique = true)
  private Long meetingId;

  @Column(name = "flash_id", unique = true)
  private Long flashId;

  @Convert(converter = WelcomeMessageTypeListConverter.class)
  @Column(name = "welcome_message_types", nullable = false, columnDefinition = "TEXT")
  private List<WelcomeMessageType> welcomeMessageTypes;

  @Convert(converter = MeetingKeywordTypeListConverter.class)
  @Column(name = "meeting_keyword_types", nullable = false, columnDefinition = "TEXT")
  private List<MeetingKeywordType> meetingKeywordTypes;

  @Builder(access = PRIVATE)
  private MeetingTagEntity(
      Long id,
      MeetingTagType type,
      Long meetingId,
      Long flashId,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {
    this.id = id;
    this.type = type;
    this.meetingId = meetingId;
    this.flashId = flashId;
    this.welcomeMessageTypes = welcomeMessageTypes;
    this.meetingKeywordTypes = meetingKeywordTypes;
  }

  public MeetingTag toDomain() {
    return new MeetingTag(
        id,
        type,
        meetingId,
        flashId,
        welcomeMessageTypes,
        meetingKeywordTypes,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static MeetingTagEntity fromDomain(MeetingTag meetingTag) {
    return MeetingTagEntity.builder()
        .id(meetingTag.id())
        .type(meetingTag.type())
        .meetingId(meetingTag.meetingId())
        .flashId(meetingTag.flashId())
        .welcomeMessageTypes(meetingTag.welcomeMessageTypes())
        .meetingKeywordTypes(meetingTag.meetingKeywordTypes())
        .build();
  }
}
