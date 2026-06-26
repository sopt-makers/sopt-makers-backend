package org.sopt.makers.domain.official.review;

import java.util.List;

public record ReviewAuthorReviews(int reviewCount, List<Review> reviews) {

  public static ReviewAuthorReviews from(List<Review> reviews) {
    return new ReviewAuthorReviews(reviews.size(), reviews);
  }
}
