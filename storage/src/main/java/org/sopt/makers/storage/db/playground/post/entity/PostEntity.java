package org.sopt.makers.storage.db.playground.post.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostCategory;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "community_post")
public class PostEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "writer_id")
  private Long writerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private PostCategory category;

  @Enumerated(EnumType.STRING)
  @Column(name = "content_type", nullable = false)
  private PostContentType contentType;

  @Column(name = "meeting_id")
  private Long meetingId;

  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String contents;

  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(name = "images", columnDefinition = "text[]")
  private List<String> images;

  @Column(name = "hits", nullable = false)
  private int viewCount;

  @Column(name = "comment_count", nullable = false)
  private int commentCount;

  @Column(name = "like_count", nullable = false)
  private int likeCount;

  @Column(name = "is_question", nullable = false)
  private boolean isQuestion;

  @Column(name = "is_blind_writer", nullable = false)
  private boolean isAnonymous;

  @Column(name = "is_reported", nullable = false)
  private boolean isReported;

  @Column(name = "is_hot", nullable = false)
  private boolean isHot;

  @Builder(access = PRIVATE)
  private PostEntity(
      Long id,
      Long writerId,
      PostCategory category,
      PostContentType contentType,
      Long meetingId,
      String title,
      String contents,
      List<String> images,
      int viewCount,
      int commentCount,
      int likeCount,
      boolean isQuestion,
      boolean isAnonymous,
      boolean isReported,
      boolean isHot) {
    this.id = id;
    this.writerId = writerId;
    this.category = category;
    this.contentType = contentType;
    this.meetingId = meetingId;
    this.title = title;
    this.contents = contents;
    this.images = images;
    this.viewCount = viewCount;
    this.commentCount = commentCount;
    this.likeCount = likeCount;
    this.isQuestion = isQuestion;
    this.isAnonymous = isAnonymous;
    this.isReported = isReported;
    this.isHot = isHot;
  }

  public Post toDomain() {
    return new Post(
        id,
        writerId,
        category,
        contentType,
        meetingId,
        title,
        contents,
        images,
        viewCount,
        commentCount,
        likeCount,
        isQuestion,
        isAnonymous,
        isReported,
        isHot,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static PostEntity fromDomain(Post post) {
    return PostEntity.builder()
        .id(post.id())
        .writerId(post.writerId())
        .category(post.category())
        .contentType(post.contentType())
        .meetingId(post.meetingId())
        .title(post.title())
        .contents(post.contents())
        .images(post.images())
        .viewCount(post.viewCount())
        .commentCount(post.commentCount())
        .likeCount(post.likeCount())
        .isQuestion(post.isQuestion())
        .isAnonymous(post.isAnonymous())
        .isReported(post.isReported())
        .isHot(post.isHot())
        .build();
  }
}
