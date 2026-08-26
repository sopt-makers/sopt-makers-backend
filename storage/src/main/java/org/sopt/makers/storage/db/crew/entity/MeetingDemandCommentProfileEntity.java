package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentProfile;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "meeting_demand_comment_profile",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_meeting_demand_comment_profile_demand_user",
            columnNames = {"meeting_demand_id", "user_id"}))
public class MeetingDemandCommentProfileEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meeting_demand_id", nullable = false)
  private Long meetingDemandId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "anonymous_nickname", nullable = false, length = 30)
  private String anonymousNickname;

  @Column(name = "anonymous_image_number", nullable = false)
  private Integer anonymousImageNumber;

  @Builder(access = PRIVATE)
  private MeetingDemandCommentProfileEntity(
      Long id,
      Long meetingDemandId,
      Long userId,
      String anonymousNickname,
      Integer anonymousImageNumber) {
    this.id = id;
    this.meetingDemandId = meetingDemandId;
    this.userId = userId;
    this.anonymousNickname = anonymousNickname;
    this.anonymousImageNumber = anonymousImageNumber;
  }

  public MeetingDemandCommentProfile toDomain() {
    return new MeetingDemandCommentProfile(
        id,
        meetingDemandId,
        userId,
        anonymousNickname,
        anonymousImageNumber,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static MeetingDemandCommentProfileEntity fromDomain(MeetingDemandCommentProfile profile) {
    return MeetingDemandCommentProfileEntity.builder()
        .id(profile.id())
        .meetingDemandId(profile.meetingDemandId())
        .userId(profile.userId())
        .anonymousNickname(profile.anonymousNickname())
        .anonymousImageNumber(profile.anonymousImageNumber())
        .build();
  }
}
