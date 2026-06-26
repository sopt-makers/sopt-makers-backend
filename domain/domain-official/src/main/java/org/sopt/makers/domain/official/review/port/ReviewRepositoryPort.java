package org.sopt.makers.domain.official.review.port;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.review.Review;
import org.sopt.makers.domain.official.review.ReviewSearchCondition;

public interface ReviewRepositoryPort {

  boolean existsByUrl(String url);

  Review save(Review review);

  List<Review> findAllWithFilters(ReviewSearchCondition condition, long offset, int limit);

  long countWithFilters(ReviewSearchCondition condition);

  Review findRandomReviewByPart(Part part);

  List<Review> findAllByAuthor(String author);
}
