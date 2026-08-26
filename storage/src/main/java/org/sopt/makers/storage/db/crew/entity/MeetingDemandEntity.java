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
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandJoinInfo;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandStatus;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.crew.converter.MeetingDemandJoinInfoConverter;
import org.sopt.makers.storage.db.crew.converter.MeetingKeywordTypeListConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "meeting_demand")
public class MeetingDemandEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "short_intro", nullable = false, length = 30)
  private String shortIntro;

  @Column(nullable = false, length = 1000)
  private String expectation;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MeetingDemandStatus status;

  @Column(name = "anonymous_nickname", nullable = false, length = 30)
  private String anonymousNickname;

  @Column(name = "anonymous_image_number", nullable = false)
  private Integer anonymousImageNumber;

  @Convert(converter = MeetingKeywordTypeListConverter.class)
  @Column(name = "meeting_keyword_types", nullable = false, columnDefinition = "TEXT")
  private List<MeetingKeywordType> meetingKeywordTypes;

  @Convert(converter = MeetingDemandJoinInfoConverter.class)
  @Column(name = "join_info", columnDefinition = "TEXT")
  private MeetingDemandJoinInfo joinInfo;

  @Column(name = "wait_count", nullable = false)
  private int waitCount;

  @Column(name = "comment_count", nullable = false)
  private int commentCount;

  @Builder(access = PRIVATE)
  private MeetingDemandEntity(
      Long id,
      Long userId,
      String shortIntro,
      String expectation,
      MeetingDemandStatus status,
      String anonymousNickname,
      Integer anonymousImageNumber,
      List<MeetingKeywordType> meetingKeywordTypes,
      MeetingDemandJoinInfo joinInfo,
      int waitCount,
      int commentCount) {
    this.id = id;
    this.userId = userId;
    this.shortIntro = shortIntro;
    this.expectation = expectation;
    this.status = status;
    this.anonymousNickname = anonymousNickname;
    this.anonymousImageNumber = anonymousImageNumber;
    this.meetingKeywordTypes = meetingKeywordTypes;
    this.joinInfo = joinInfo;
    this.waitCount = waitCount;
    this.commentCount = commentCount;
  }

  public MeetingDemand toDomain() {
    return new MeetingDemand(
        id,
        userId,
        shortIntro,
        expectation,
        status,
        anonymousNickname,
        anonymousImageNumber,
        meetingKeywordTypes,
        joinInfo,
        waitCount,
        commentCount,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static MeetingDemandEntity fromDomain(MeetingDemand demand) {
    return MeetingDemandEntity.builder()
        .id(demand.id())
        .userId(demand.userId())
        .shortIntro(demand.shortIntro())
        .expectation(demand.expectation())
        .status(demand.status())
        .anonymousNickname(demand.anonymousNickname())
        .anonymousImageNumber(demand.anonymousImageNumber())
        .meetingKeywordTypes(demand.meetingKeywordTypes())
        .joinInfo(demand.joinInfo())
        .waitCount(demand.waitCount())
        .commentCount(demand.commentCount())
        .build();
  }
}
