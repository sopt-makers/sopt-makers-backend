package org.sopt.makers.storage.db.playground.community.comment.entity;

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
import org.sopt.makers.domain.playground.community.comment.Comment;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "community_comment")
public class CommentEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "writer_id", nullable = false)
  private Long writerId;

  @Column(name = "parent_comment_id")
  private Long parentCommentId;

  @Column(name = "is_blind_writer", nullable = false)
  private Boolean isBlindWriter;

  @Column(name = "is_reported", nullable = false)
  private Boolean isReported;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @Column(name = "anonymous_profile_id")
  private Long anonymousProfileId;

  @Builder(access = PROTECTED)
  private CommentEntity(
      String content,
      Long postId,
      Long writerId,
      Long parentCommentId,
      Boolean isBlindWriter,
      Boolean isReported,
      Boolean isDeleted,
      Long anonymousProfileId) {
    this.content = content;
    this.postId = postId;
    this.writerId = writerId;
    this.parentCommentId = parentCommentId;
    this.isBlindWriter = isBlindWriter;
    this.isReported = isReported;
    this.isDeleted = isDeleted;
    this.anonymousProfileId = anonymousProfileId;
  }

  public static CommentEntity from(Comment comment) {
    return CommentEntity.builder()
        .content(comment.content())
        .postId(comment.postId())
        .writerId(comment.writerId())
        .parentCommentId(comment.parentCommentId())
        .isBlindWriter(comment.isBlindWriter())
        .isReported(comment.isReported())
        .isDeleted(comment.isDeleted())
        .anonymousProfileId(comment.anonymousProfileId())
        .build();
  }

  /** 기존 관리 대상(managed) 엔티티에 도메인 변경분을 반영한다. (CUD - 댓글 내용 수정/삭제 처리/익명 프로필 연결) */
  public void applyChanges(Comment comment) {
    this.content = comment.content();
    this.isReported = comment.isReported();
    this.isDeleted = comment.isDeleted();
    this.anonymousProfileId = comment.anonymousProfileId();
  }

  public Comment toDomain() {
    return new Comment(
        id,
        content,
        postId,
        writerId,
        parentCommentId,
        isBlindWriter,
        isReported,
        isDeleted,
        anonymousProfileId,
        getCreatedAt(),
        getUpdatedAt());
  }
}
