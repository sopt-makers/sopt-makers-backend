package org.sopt.makers.storage.db.playground.coffeechat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.CoffeeChatReview;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coffee_chat_review")
public class CoffeeChatReviewEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "reviewer_id")
  private Long reviewerId;

  @Column(name = "coffee_chat_id")
  private Long coffeeChatId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "anonymous_profile_image")
  private AnonymousProfileImageEntity anonymousProfileImage;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  private CoffeeChatReviewEntity(
      Long reviewerId,
      Long coffeeChatId,
      AnonymousProfileImageEntity anonymousProfileImage,
      String nickname,
      String content) {
    this.reviewerId = reviewerId;
    this.coffeeChatId = coffeeChatId;
    this.anonymousProfileImage = anonymousProfileImage;
    this.nickname = nickname;
    this.content = content;
  }

  public static CoffeeChatReviewEntity of(
      Long reviewerId,
      Long coffeeChatId,
      AnonymousProfileImageEntity anonymousProfileImage,
      String nickname,
      String content) {
    return new CoffeeChatReviewEntity(reviewerId, coffeeChatId, anonymousProfileImage, nickname, content);
  }

  public CoffeeChatReview toDomain() {
    return new CoffeeChatReview(
        id,
        reviewerId,
        coffeeChatId,
        anonymousProfileImage != null ? anonymousProfileImage.getImageUrl() : null,
        nickname,
        content);
  }
}
