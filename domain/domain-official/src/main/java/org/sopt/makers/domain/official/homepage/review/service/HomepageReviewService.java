package org.sopt.makers.domain.official.homepage.review.service;

import static org.sopt.makers.domain.official.homepage.review.exception.HomepageReviewFailure.EXCEEDED_HOMEPAGE_REVIEW_COUNT;
import static org.sopt.makers.domain.official.homepage.review.exception.HomepageReviewFailure.NOT_FOUND_HOMEPAGE_REVIEW;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;
import org.sopt.makers.domain.official.homepage.review.exception.HomepageReviewException;
import org.sopt.makers.domain.official.homepage.review.port.HomepageReviewRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomepageReviewService {

  private static final int MAX_REVIEW = 7;

  private final HomepageReviewRepositoryPort homepageReviewRepositoryPort;

  @Transactional
  public HomepageReview create(CreateHomepageReviewCommand command) {
    HomepageReview review =
        HomepageReview.create(command.title(), command.content(), command.authorInfo());
    return homepageReviewRepositoryPort.save(review);
  }

  @Transactional
  public List<HomepageReview> createAll(BulkCreateHomepageReviewsCommand command) {
    List<HomepageReview> reviews =
        command.reviews().stream()
            .map(
                review ->
                    HomepageReview.create(review.title(), review.content(), review.authorInfo()))
            .toList();
    return homepageReviewRepositoryPort.saveAll(reviews);
  }

  @Transactional
  public List<HomepageReview> replaceAll(BulkCreateHomepageReviewsCommand command) {
    homepageReviewRepositoryPort.deleteAll();
    return createAll(command);
  }

  public List<HomepageReview> getReviews() {
    List<HomepageReview> reviews = homepageReviewRepositoryPort.findAllByOrderByIdAsc();

    if (reviews.size() > MAX_REVIEW) {
      throw new HomepageReviewException(EXCEEDED_HOMEPAGE_REVIEW_COUNT);
    }

    return reviews;
  }

  @Transactional
  public HomepageReview edit(Long id, EditHomepageReviewCommand command) {
    HomepageReview review =
        homepageReviewRepositoryPort
            .findById(id)
            .orElseThrow(() -> new HomepageReviewException(NOT_FOUND_HOMEPAGE_REVIEW));

    return homepageReviewRepositoryPort.save(
        review.update(command.title(), command.content(), command.authorInfo()));
  }

  public record CreateHomepageReviewCommand(String title, String content, String authorInfo) {}

  public record EditHomepageReviewCommand(String title, String content, String authorInfo) {}

  public record BulkCreateHomepageReviewsCommand(List<ReviewData> reviews) {

    public record ReviewData(String title, String content, String authorInfo) {}
  }
}
