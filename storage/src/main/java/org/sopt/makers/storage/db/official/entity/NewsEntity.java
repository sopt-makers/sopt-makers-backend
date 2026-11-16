package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.sopt.makers.domain.official.news.News;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "main_news")
// DB 스키마명 불일치 이슈로 BaseEntity 상속하지 않음
public class NewsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "image", nullable = false)
  private String image;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "link", nullable = false)
  private String link;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Column(name = "hidden", nullable = false)
  private boolean hidden;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Builder(access = PRIVATE)
  private NewsEntity(
      Integer id,
      String image,
      String title,
      String link,
      Instant publishedAt,
      boolean hidden,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.image = image;
    this.title = title;
    this.link = link;
    this.publishedAt = publishedAt;
    this.hidden = hidden;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  @PrePersist
  private void initializePublishedAt() {
    if (publishedAt == null) {
      publishedAt = Instant.now();
    }
  }

  public News toDomain() {
    return new News(id, image, title, link, publishedAt, hidden, createdAt, updatedAt);
  }

  public static NewsEntity fromDomain(News news) {
    return NewsEntity.builder()
        .id(news.id())
        .image(news.imageUrl())
        .title(news.title())
        .link(news.link())
        .publishedAt(news.publishedAt())
        .hidden(news.hidden())
        .createdAt(news.createdAt())
        .updatedAt(news.updatedAt())
        .build();
  }
}
