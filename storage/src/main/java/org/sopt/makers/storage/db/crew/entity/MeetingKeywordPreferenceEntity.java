package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordPreference;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.crew.converter.MeetingKeywordTypeListConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "meeting_keyword_preference")
public class MeetingKeywordPreferenceEntity extends BaseEntity {

  @Id
  @Column(name = "user_id")
  private Long userId;

  @Convert(converter = MeetingKeywordTypeListConverter.class)
  @Column(name = "keyword_types", nullable = false, columnDefinition = "TEXT")
  private List<MeetingKeywordType> keywordTypes;

  @Builder(access = PRIVATE)
  private MeetingKeywordPreferenceEntity(Long userId, List<MeetingKeywordType> keywordTypes) {
    this.userId = userId;
    this.keywordTypes = keywordTypes;
  }

  public MeetingKeywordPreference toDomain() {
    return new MeetingKeywordPreference(userId, keywordTypes);
  }

  public static MeetingKeywordPreferenceEntity fromDomain(MeetingKeywordPreference preference) {
    return MeetingKeywordPreferenceEntity.builder()
        .userId(preference.userId())
        .keywordTypes(preference.keywordTypes())
        .build();
  }
}
