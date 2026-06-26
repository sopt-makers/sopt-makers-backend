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
import org.sopt.makers.domain.official.soptstory.SoptStory;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "\"SoptStory\"")
public class SoptStoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Long id;

  @Column(name = "\"title\"", nullable = false, length = 100)
  private String title;

  @Column(name = "\"description\"", nullable = false, length = 600)
  private String description;

  @Column(name = "\"thumbnailUrl\"", length = 500)
  private String thumbnailUrl;

  @Column(name = "\"soptStoryUrl\"", nullable = false, length = 500)
  private String url;

  @Column(name = "\"likeCount\"", nullable = false)
  private int likeCount;

  @CreationTimestamp
  @Column(name = "\"createdAt\"", nullable = false)
  private LocalDateTime createdAt;

  @Builder(access = PRIVATE)
  private SoptStoryEntity(
      Long id,
      String title,
      String description,
      String thumbnailUrl,
      String url,
      int likeCount,
      LocalDateTime createdAt) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.thumbnailUrl = thumbnailUrl;
    this.url = url;
    this.likeCount = likeCount;
    this.createdAt = createdAt;
  }

  public SoptStory toDomain() {
    return new SoptStory(id, title, description, thumbnailUrl, url, likeCount, createdAt);
  }

  public static SoptStoryEntity fromDomain(SoptStory soptStory) {
    return SoptStoryEntity.builder()
        .id(soptStory.id())
        .title(soptStory.title())
        .description(soptStory.description())
        .thumbnailUrl(soptStory.thumbnailUrl())
        .url(soptStory.url())
        .likeCount(soptStory.likeCount())
        .createdAt(soptStory.createdAt())
        .build();
  }

  public static SoptStoryEntity reference(Long id) {
    return SoptStoryEntity.builder().id(id).build();
  }
}
