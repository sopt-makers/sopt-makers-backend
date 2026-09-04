package org.sopt.makers.storage.db.playground.community.post.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.playground.community.entity.CategoryEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "community_post")
public class PostEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "writer_id", nullable = false)
  private Long writerId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Column(name = "title")
  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "hits", nullable = false)
  private Integer hits;

  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(name = "images", columnDefinition = "text[]")
  private List<String> images;

  @Column(name = "is_question", nullable = false)
  private Boolean isQuestion;

  @Column(name = "is_blind_writer", nullable = false)
  private Boolean isBlindWriter;

  @Column(name = "is_reported", nullable = false)
  private Boolean isReported;

  @Column(name = "is_hot", nullable = false)
  private Boolean isHot;

  @Column(name = "sopticle_url")
  private String sopticleUrl;

  @Column(name = "anonymous_profile_id")
  private Long anonymousProfileId;

  @Builder(access = PROTECTED)
  private PostEntity(
      Long writerId,
      CategoryEntity category,
      String title,
      String content,
      Integer hits,
      List<String> images,
      Boolean isQuestion,
      Boolean isBlindWriter,
      Boolean isReported,
      Boolean isHot,
      String sopticleUrl,
      Long anonymousProfileId) {
    this.writerId = writerId;
    this.category = category;
    this.title = title;
    this.content = content;
    this.hits = hits;
    this.images = images;
    this.isQuestion = isQuestion;
    this.isBlindWriter = isBlindWriter;
    this.isReported = isReported;
    this.isHot = isHot;
    this.sopticleUrl = sopticleUrl;
    this.anonymousProfileId = anonymousProfileId;
  }

  public static PostEntity of(Post post, CategoryEntity category) {
    return PostEntity.builder()
        .writerId(post.writerId())
        .category(category)
        .title(post.title())
        .content(post.content())
        .hits(post.hits())
        .images(post.images())
        .isQuestion(post.isQuestion())
        .isBlindWriter(post.isBlindWriter())
        .isReported(post.isReported())
        .isHot(post.isHot())
        .sopticleUrl(post.sopticleUrl())
        .anonymousProfileId(post.anonymousProfileId())
        .build();
  }

  /** 기존 관리 대상(managed) 엔티티에 도메인 변경분을 반영한다. (CUD - 게시글 수정/익명 프로필 연결) */
  public void applyChanges(Post post, CategoryEntity category) {
    this.category = category;
    this.title = post.title();
    this.content = post.content();
    this.images = post.images();
    this.isQuestion = post.isQuestion();
    this.isBlindWriter = post.isBlindWriter();
    this.sopticleUrl = post.sopticleUrl();
    this.anonymousProfileId = post.anonymousProfileId();
  }

  public Post toDomain() {
    return new Post(
        id,
        writerId,
        category.getId(),
        title,
        content,
        hits,
        images,
        isQuestion,
        isBlindWriter,
        isReported,
        isHot,
        sopticleUrl,
        anonymousProfileId,
        getCreatedAt(),
        getUpdatedAt());
  }
}
