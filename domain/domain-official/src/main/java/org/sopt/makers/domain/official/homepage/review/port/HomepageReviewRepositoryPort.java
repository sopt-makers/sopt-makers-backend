package org.sopt.makers.domain.official.homepage.review.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;

public interface HomepageReviewRepositoryPort {

  HomepageReview save(HomepageReview review);

  List<HomepageReview> saveAll(List<HomepageReview> reviews);

  Optional<HomepageReview> findById(Long id);

  List<HomepageReview> findAllByOrderByIdAsc();
}
