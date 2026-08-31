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
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentLike;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "meeting_demand_comment_like",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_meeting_demand_comment_like_comment_user",
            columnNames = {"meeting_demand_comment_id", "user_id"}))
public class MeetingDemandCommentLikeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meeting_demand_comment_id", nullable = false)
  private Long meetingDemandCommentId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Builder(access = PRIVATE)
  private MeetingDemandCommentLikeEntity(Long id, Long meetingDemandCommentId, Long userId) {
    this.id = id;
    this.meetingDemandCommentId = meetingDemandCommentId;
    this.userId = userId;
  }

  public MeetingDemandCommentLike toDomain() {
    return new MeetingDemandCommentLike(
        id, meetingDemandCommentId, userId, getCreatedAt(), getUpdatedAt());
  }

  public static MeetingDemandCommentLikeEntity fromDomain(MeetingDemandCommentLike like) {
    return MeetingDemandCommentLikeEntity.builder()
        .id(like.id())
        .meetingDemandCommentId(like.meetingDemandCommentId())
        .userId(like.userId())
        .build();
  }
}
