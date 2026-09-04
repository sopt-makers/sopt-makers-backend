package org.sopt.makers.storage.db.playground.community.comment.entity;

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
import org.sopt.makers.domain.playground.community.comment.CommentLike;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "community_comment_like",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_comment_like_member_comment", columnNames = {"member_id", "comment_id"})
    })
public class CommentLikeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "community_comment_like_id")
  private Long id;

  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @Column(name = "comment_id", nullable = false)
  private Long commentId;

  @Builder(access = PROTECTED)
  private CommentLikeEntity(Long memberId, Long commentId) {
    this.memberId = memberId;
    this.commentId = commentId;
  }

  public static CommentLikeEntity from(CommentLike commentLike) {
    return CommentLikeEntity.builder().memberId(commentLike.memberId()).commentId(commentLike.commentId()).build();
  }

  public CommentLike toDomain() {
    return new CommentLike(id, memberId, commentId, getCreatedAt(), getUpdatedAt());
  }
}
