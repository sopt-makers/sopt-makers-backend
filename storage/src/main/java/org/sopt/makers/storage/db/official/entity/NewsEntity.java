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
import org.sopt.makers.domain.official.news.News;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "\"MainNews\"")
// DB 스키마명 불일치 이슈로 BaseEntity 상속하지 않음
public class NewsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Integer id;

  @Column(name = "\"image\"", nullable = false)
  private String image;

  @Column(name = "\"title\"", nullable = false)
  private String title;

  @Column(name = "\"link\"", nullable = false)
  private String link;

  @CreationTimestamp
  @Column(name = "\"createdAt\"", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "\"updatedAt\"", nullable = false)
  private LocalDateTime updatedAt;

  @Builder(access = PRIVATE)
  private NewsEntity(
      Integer id,
      String image,
      String title,
      String link,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.image = image;
    this.title = title;
    this.link = link;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public News toDomain() {
    return new News(id, image, title, link, createdAt, updatedAt);
  }

  public static NewsEntity fromDomain(News news) {
    return NewsEntity.builder()
        .id(news.id())
        .image(news.imageUrl())
        .title(news.title())
        .link(news.link())
        .createdAt(news.createdAt())
        .updatedAt(news.updatedAt())
        .build();
  }
}
