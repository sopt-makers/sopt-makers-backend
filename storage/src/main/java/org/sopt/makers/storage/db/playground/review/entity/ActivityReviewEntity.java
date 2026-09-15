package org.sopt.makers.storage.db.playground.review.entity;

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
import org.sopt.makers.domain.playground.review.ActivityReview;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Table(name = "activity_review")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ActivityReviewEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private Integer generation;

  @Builder
  private ActivityReviewEntity(Long userId, String content, Integer generation) {
    this.userId = userId;
    this.content = content;
    this.generation = generation;
  }

  public static ActivityReviewEntity from(ActivityReview review) {
    return ActivityReviewEntity.builder()
        .userId(review.userId())
        .content(review.content())
        .generation(review.generation())
        .build();
  }

  public ActivityReview toDomain() {
    return new ActivityReview(getId(), userId, content, generation, getCreatedAt());
  }
}
