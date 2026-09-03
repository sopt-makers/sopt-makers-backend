package org.sopt.makers.storage.db.playground.post.entity;

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
import org.sopt.makers.domain.playground.post.comment.PostComment;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "community_comment")
public class PostCommentEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "writer_id")
  private Long writerId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String contents;

  @Column(name = "parent_comment_id")
  private Long parentCommentId;

  @Column(nullable = false)
  private int depth;

  @Column(name = "comment_order", nullable = false)
  private int order;

  @Column(name = "like_count", nullable = false)
  private int likeCount;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

  @Builder(access = PRIVATE)
  private PostCommentEntity(
      Long id,
      Long postId,
      Long writerId,
      String contents,
      Long parentCommentId,
      int depth,
      int order,
      int likeCount,
      boolean isDeleted) {
    this.id = id;
    this.postId = postId;
    this.writerId = writerId;
    this.contents = contents;
    this.parentCommentId = parentCommentId;
    this.depth = depth;
    this.order = order;
    this.likeCount = likeCount;
    this.isDeleted = isDeleted;
  }

  public PostComment toDomain() {
    return new PostComment(
        id,
        postId,
        writerId,
        contents,
        parentCommentId,
        depth,
        order,
        likeCount,
        isDeleted,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static PostCommentEntity fromDomain(PostComment comment) {
    return PostCommentEntity.builder()
        .id(comment.id())
        .postId(comment.postId())
        .writerId(comment.writerId())
        .contents(comment.contents())
        .parentCommentId(comment.parentCommentId())
        .depth(comment.depth())
        .order(comment.order())
        .likeCount(comment.likeCount())
        .isDeleted(comment.isDeleted())
        .build();
  }
}
