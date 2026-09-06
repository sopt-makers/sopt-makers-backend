package org.sopt.makers.storage.db.playground.post.entity;

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
import org.sopt.makers.domain.playground.post.like.PostCommentLike;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "community_comment_like",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_community_comment_like_comment_user",
            columnNames = {"comment_id", "user_id"}))
public class PostCommentLikeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "community_comment_like_id")
  private Long id;

  @Column(name = "comment_id", nullable = false)
  private Long commentId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Builder(access = PRIVATE)
  private PostCommentLikeEntity(Long id, Long commentId, Long userId) {
    this.id = id;
    this.commentId = commentId;
    this.userId = userId;
  }

  public PostCommentLike toDomain() {
    return new PostCommentLike(id, commentId, userId, getCreatedAt(), getUpdatedAt());
  }

  public static PostCommentLikeEntity fromDomain(PostCommentLike like) {
    return PostCommentLikeEntity.builder()
        .id(like.id())
        .commentId(like.commentId())
        .userId(like.userId())
        .build();
  }
}
