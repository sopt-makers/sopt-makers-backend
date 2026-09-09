package org.sopt.makers.domain.playground.review.port;

import java.util.List;
import org.sopt.makers.domain.playground.review.ActivityReview;

public interface ActivityReviewRepositoryPort {

  ActivityReview save(ActivityReview review);

  ActivityReviewPage findAllByGeneration(int generation, int page, int size);

  record ActivityReviewPage(List<ActivityReview> reviews, boolean hasNext) {}
}
