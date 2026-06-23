package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PRIVATE;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "\"HomepageReview\"")
// DB 스키마명 불일치 이슈로 BaseEntity 상속하지 않음
public class HomepageReviewEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Long id;

  @Column(name = "\"title\"", nullable = false)
  private String title;

  @Column(name = "\"content\"", nullable = false, length = 200)
  private String content;

  @Column(name = "\"authorInfo\"", nullable = false)
  private String authorInfo;

  @CreationTimestamp
  @Column(name = "\"createdAt\"", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "\"updatedAt\"", nullable = false)
  private LocalDateTime updatedAt;

  @Builder(access = PRIVATE)
  private HomepageReviewEntity(
      Long id,
      String title,
      String content,
      String authorInfo,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.authorInfo = authorInfo;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public HomepageReview toDomain() {
    return new HomepageReview(id, title, content, authorInfo, createdAt, updatedAt);
  }

  public static HomepageReviewEntity fromDomain(HomepageReview review) {
    return HomepageReviewEntity.builder()
        .id(review.id())
        .title(review.title())
        .content(review.content())
        .authorInfo(review.authorInfo())
        .createdAt(review.createdAt())
        .updatedAt(review.updatedAt())
        .build();
  }
}
