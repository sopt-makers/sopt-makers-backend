package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandComment;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "meeting_demand_comment")
public class MeetingDemandCommentEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meeting_demand_id", nullable = false)
  private Long meetingDemandId;

  @Column(name = "user_id")
  private Long userId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String contents;

  @Column(nullable = false)
  private int depth;

  @Column(name = "comment_order", nullable = false)
  private int order;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "like_count", nullable = false)
  private int likeCount;

  @Builder(access = PRIVATE)
  private MeetingDemandCommentEntity(
      Long id,
      Long meetingDemandId,
      Long userId,
      String contents,
      int depth,
      int order,
      Long parentId,
      int likeCount) {
    this.id = id;
    this.meetingDemandId = meetingDemandId;
    this.userId = userId;
    this.contents = contents;
    this.depth = depth;
    this.order = order;
    this.parentId = parentId;
    this.likeCount = likeCount;
  }

  public MeetingDemandComment toDomain() {
    return new MeetingDemandComment(
        id,
        meetingDemandId,
        userId,
        contents,
        depth,
        order,
        parentId,
        likeCount,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static MeetingDemandCommentEntity fromDomain(MeetingDemandComment comment) {
    return MeetingDemandCommentEntity.builder()
        .id(comment.id())
        .meetingDemandId(comment.meetingDemandId())
        .userId(comment.userId())
        .contents(comment.contents())
        .depth(comment.depth())
        .order(comment.order())
        .parentId(comment.parentId())
        .likeCount(comment.likeCount())
        .build();
  }
}
