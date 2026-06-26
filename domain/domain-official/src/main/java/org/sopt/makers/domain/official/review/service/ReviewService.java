package org.sopt.makers.domain.official.review.service;

import static org.sopt.makers.domain.official.review.exception.ReviewFailure.DUPLICATE_REVIEW_URL;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.review.CreateReviewCommand;
import org.sopt.makers.domain.official.review.Review;
import org.sopt.makers.domain.official.review.ReviewAuthorReviews;
import org.sopt.makers.domain.official.review.ReviewPart;
import org.sopt.makers.domain.official.review.ReviewSearchCondition;
import org.sopt.makers.domain.official.review.ScrapedReviewArticle;
import org.sopt.makers.domain.official.review.exception.ReviewException;
import org.sopt.makers.domain.official.review.port.ReviewRepositoryPort;
import org.sopt.makers.domain.official.review.port.ReviewScraperPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

  private final ReviewRepositoryPort reviewRepositoryPort;
  private final ReviewScraperPort reviewScraperPort;

  @Transactional
  public Long create(CreateReviewCommand command) {
    ScrapedReviewArticle article = reviewScraperPort.scrapReview(command.link());
    String url = article.articleUrl();
    if (reviewRepositoryPort.existsByUrl(url)) {
      throw new ReviewException(DUPLICATE_REVIEW_URL);
    }
    Review review =
        Review.create(
            article.title(),
            article.description(),
            article.thumbnailUrl(),
            article.platform(),
            command.author(),
            command.authorProfileImageUrl(),
            command.generation(),
            command.part(),
            command.categoryType(),
            command.subjects(),
            url);
    return reviewRepositoryPort.save(review).id();
  }

  public List<Review> search(ReviewSearchCondition condition, long offset, int limit) {
    return reviewRepositoryPort.findAllWithFilters(condition, offset, limit);
  }

  public long count(ReviewSearchCondition condition) {
    return reviewRepositoryPort.countWithFilters(condition);
  }

  public List<Review> getRandomReviewsByPart() {
    return ReviewPart.homepageParts().stream()
        .map(reviewRepositoryPort::findRandomReviewByPart)
        .filter(review -> review != null)
        .toList();
  }

  public ReviewAuthorReviews getReviewsByAuthor(String author) {
    return ReviewAuthorReviews.from(reviewRepositoryPort.findAllByAuthor(author));
  }
}
