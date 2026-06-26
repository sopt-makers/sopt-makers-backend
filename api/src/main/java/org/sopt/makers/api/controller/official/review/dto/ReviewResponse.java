package org.sopt.makers.api.controller.official.review.dto;

import java.util.List;
import org.sopt.makers.domain.official.review.Review;
import org.sopt.makers.domain.official.review.ReviewAuthorReviews;
import org.sopt.makers.domain.official.review.ReviewPart;

public record ReviewResponse(
    Long id,
    String title,
    String author,
    String authorProfileImageUrl,
    Integer generation,
    String description,
    String partType,
    String category,
    List<String> subject,
    String thumbnailUrl,
    String platform,
    String url) {

  public static ReviewResponse of(Review review) {
    return new ReviewResponse(
        review.id(),
        review.title(),
        review.author(),
        review.authorProfileImageUrl(),
        review.generation(),
        review.description(),
        ReviewPart.displayName(review.part()),
        review.category().getDisplayName(),
        review.subjects(),
        review.thumbnailUrl(),
        review.platform(),
        review.url());
  }

  public static List<ReviewResponse> of(List<Review> reviews) {
    return reviews.stream().map(ReviewResponse::of).toList();
  }

  public record Create(Long id) {}

  public record Page(List<ReviewResponse> data, int totalCount, int limit, int pageNo) {
    public static Page of(List<ReviewResponse> data, int totalCount, int limit, int pageNo) {
      return new Page(data, totalCount, limit, pageNo);
    }
  }

  public record AuthorReviews(int reviewCount, List<ReviewResponse> reviews) {
    public static AuthorReviews of(ReviewAuthorReviews reviewAuthorReviews) {
      return new AuthorReviews(
          reviewAuthorReviews.reviewCount(), ReviewResponse.of(reviewAuthorReviews.reviews()));
    }
  }
}
