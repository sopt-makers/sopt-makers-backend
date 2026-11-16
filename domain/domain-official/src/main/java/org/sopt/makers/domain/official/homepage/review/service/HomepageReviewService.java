package org.sopt.makers.domain.official.homepage.review.service;

import static org.sopt.makers.domain.official.homepage.review.exception.HomepageReviewFailure.EXCEEDED_HOMEPAGE_REVIEW_COUNT;
import static org.sopt.makers.domain.official.homepage.review.exception.HomepageReviewFailure.NOT_FOUND_HOMEPAGE_REVIEW;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;
import org.sopt.makers.domain.official.homepage.review.HomepageReviewType;
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
    HomepageReviewType type = command.type() == null ? HomepageReviewType.ACTIVITY : command.type();
    validateCapacity(type);
    HomepageReview review =
        HomepageReview.create(type, command.title(), command.content(), command.authorInfo());
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

  public List<HomepageReview> findAll() {
    return homepageReviewRepositoryPort.findAllByOrderByIdAsc();
  }

  public List<HomepageReview> getReviews() {
    return findAll();
  }

  @Transactional
  public HomepageReview edit(Long id, EditHomepageReviewCommand command) {
    HomepageReview review =
        homepageReviewRepositoryPort
            .findById(id)
            .orElseThrow(() -> new HomepageReviewException(NOT_FOUND_HOMEPAGE_REVIEW));

    if (command.type() != null && command.type() != review.type()) {
      validateCapacity(command.type());
    }
    return homepageReviewRepositoryPort.save(
        review.update(command.type(), command.title(), command.content(), command.authorInfo()));
  }

  public record CreateHomepageReviewCommand(
      String title, String content, String authorInfo, HomepageReviewType type) {}

  public record EditHomepageReviewCommand(
      String title, String content, String authorInfo, HomepageReviewType type) {}

  public record BulkCreateHomepageReviewsCommand(List<ReviewData> reviews) {

    public record ReviewData(String title, String content, String authorInfo) {}
  }

  private void validateCapacity(HomepageReviewType type) {
    if (homepageReviewRepositoryPort.countByType(type) >= MAX_REVIEW) {
      throw new HomepageReviewException(EXCEEDED_HOMEPAGE_REVIEW_COUNT);
    }
  }
}
