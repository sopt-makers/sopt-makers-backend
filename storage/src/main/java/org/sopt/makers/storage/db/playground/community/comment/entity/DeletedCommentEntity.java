package org.sopt.makers.storage.db.playground.community.comment.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.community.comment.DeletedComment;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "deleted_community_comment")
// 삭제된 댓글 스냅샷 보관용 테이블로 BaseEntity(감사 컬럼)를 상속하지 않음
public class DeletedCommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "content", length = 10000)
  private String content;

  @Column(name = "post_id")
  private Long postId;

  @Column(name = "writer_id")
  private Long writerId;

  @Column(name = "parent_comment_id")
  private Long parentCommentId;

  @Column(name = "is_blind_writer")
  private Boolean isBlindWriter;

  @Column(name = "is_reported")
  private Boolean isReported;

  @Column(name = "deleted_at", nullable = false)
  private LocalDateTime deletedAt;

  @Builder(access = PROTECTED)
  private DeletedCommentEntity(
      String content,
      Long postId,
      Long writerId,
      Long parentCommentId,
      Boolean isBlindWriter,
      Boolean isReported,
      LocalDateTime deletedAt) {
    this.content = content;
    this.postId = postId;
    this.writerId = writerId;
    this.parentCommentId = parentCommentId;
    this.isBlindWriter = isBlindWriter;
    this.isReported = isReported;
    this.deletedAt = deletedAt;
  }

  public static DeletedCommentEntity from(DeletedComment deletedComment) {
    return DeletedCommentEntity.builder()
        .content(deletedComment.content())
        .postId(deletedComment.postId())
        .writerId(deletedComment.writerId())
        .parentCommentId(deletedComment.parentCommentId())
        .isBlindWriter(deletedComment.isBlindWriter())
        .isReported(deletedComment.isReported())
        .deletedAt(deletedComment.deletedAt())
        .build();
  }

  public DeletedComment toDomain() {
    return new DeletedComment(
        id, content, postId, writerId, parentCommentId, isBlindWriter, isReported, deletedAt);
  }
}
