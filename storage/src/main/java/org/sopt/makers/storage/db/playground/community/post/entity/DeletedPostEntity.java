package org.sopt.makers.storage.db.playground.community.post.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.domain.playground.community.post.DeletedPost;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "deleted_community_post")
// 삭제된 게시글 스냅샷 보관용 테이블로 BaseEntity(감사 컬럼)를 상속하지 않음
public class DeletedPostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "writer_id")
  private Long writerId;

  @Column(name = "category_id")
  private Long categoryId;

  @Column(name = "title")
  private String title;

  @Column(name = "content", length = 10000)
  private String content;

  @Column(name = "hits")
  private Integer hits;

  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(name = "images", columnDefinition = "text[]")
  private List<String> images;

  @Column(name = "is_question")
  private Boolean isQuestion;

  @Column(name = "is_blind_writer")
  private Boolean isBlindWriter;

  @Column(name = "is_reported")
  private Boolean isReported;

  @Column(name = "deleted_at", nullable = false)
  private LocalDateTime deletedAt;

  @Builder(access = PROTECTED)
  private DeletedPostEntity(
      Long writerId,
      Long categoryId,
      String title,
      String content,
      Integer hits,
      List<String> images,
      Boolean isQuestion,
      Boolean isBlindWriter,
      Boolean isReported,
      LocalDateTime deletedAt) {
    this.writerId = writerId;
    this.categoryId = categoryId;
    this.title = title;
    this.content = content;
    this.hits = hits;
    this.images = images;
    this.isQuestion = isQuestion;
    this.isBlindWriter = isBlindWriter;
    this.isReported = isReported;
    this.deletedAt = deletedAt;
  }

  public static DeletedPostEntity from(DeletedPost deletedPost) {
    return DeletedPostEntity.builder()
        .writerId(deletedPost.writerId())
        .categoryId(deletedPost.categoryId())
        .title(deletedPost.title())
        .content(deletedPost.content())
        .hits(deletedPost.hits())
        .images(deletedPost.images())
        .isQuestion(deletedPost.isQuestion())
        .isBlindWriter(deletedPost.isBlindWriter())
        .isReported(deletedPost.isReported())
        .deletedAt(deletedPost.deletedAt())
        .build();
  }

  public DeletedPost toDomain() {
    return new DeletedPost(
        id, writerId, categoryId, title, content, hits, images, isQuestion, isBlindWriter, isReported, deletedAt);
  }
}
