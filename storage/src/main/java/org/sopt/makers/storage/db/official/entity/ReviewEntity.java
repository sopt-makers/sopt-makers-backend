package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.review.CategoryType;
import org.sopt.makers.domain.official.review.Review;
import org.sopt.makers.storage.db.official.converter.CategoryTypeConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "review")
public class ReviewEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "title", nullable = false, length = 1000)
  private String title;

  @Column(name = "description", nullable = false, length = 2000)
  private String description;

  @Column(name = "thumbnail_url", length = 500)
  private String thumbnailUrl;

  @Column(name = "platform", nullable = false, length = 50)
  private String platform;

  @Column(name = "author", nullable = false, length = 20)
  private String author;

  @Column(name = "author_profile_image_url", length = 500)
  private String authorProfileImageUrl;

  @Column(name = "generation", nullable = false)
  private Integer generation;

  @Enumerated(EnumType.STRING)
  @Column(name = "part", nullable = false, length = 20)
  private Part part;

  @Convert(converter = CategoryTypeConverter.class)
  @Column(name = "category", nullable = false, length = 20)
  private CategoryType category;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "subject", nullable = false, columnDefinition = "text")
  private List<String> subjects;

  @Column(name = "url", nullable = false, length = 500)
  private String url;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Builder(access = PRIVATE)
  private ReviewEntity(
      Long id,
      String title,
      String description,
      String thumbnailUrl,
      String platform,
      String author,
      String authorProfileImageUrl,
      Integer generation,
      Part part,
      CategoryType category,
      List<String> subjects,
      String url,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.thumbnailUrl = thumbnailUrl;
    this.platform = platform;
    this.author = author;
    this.authorProfileImageUrl = authorProfileImageUrl;
    this.generation = generation;
    this.part = part;
    this.category = category;
    this.subjects = subjects;
    this.url = url;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Review toDomain() {
    return new Review(
        id,
        title,
        description,
        thumbnailUrl,
        platform,
        author,
        authorProfileImageUrl,
        generation,
        part,
        category,
        subjects,
        url,
        createdAt,
        updatedAt);
  }

  public static ReviewEntity fromDomain(Review review) {
    return ReviewEntity.builder()
        .id(review.id())
        .title(review.title())
        .description(review.description())
        .thumbnailUrl(review.thumbnailUrl())
        .platform(review.platform())
        .author(review.author())
        .authorProfileImageUrl(review.authorProfileImageUrl())
        .generation(review.generation())
        .part(review.part())
        .category(review.category())
        .subjects(review.subjects())
        .url(review.url())
        .createdAt(review.createdAt())
        .updatedAt(review.updatedAt())
        .build();
  }
}
