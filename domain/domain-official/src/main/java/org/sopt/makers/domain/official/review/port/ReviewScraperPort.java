package org.sopt.makers.domain.official.review.port;

import org.sopt.makers.domain.official.review.ScrapedReviewArticle;

public interface ReviewScraperPort {

  ScrapedReviewArticle scrapReview(String articleUrl);
}
