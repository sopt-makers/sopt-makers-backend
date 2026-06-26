package org.sopt.makers.clients.scrap;

import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.SCRAP_FAILED;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.official.review.ScrapedReviewArticle;
import org.sopt.makers.domain.official.review.exception.ReviewException;
import org.sopt.makers.domain.official.review.exception.ReviewFailure;
import org.sopt.makers.domain.official.review.port.ReviewScraperPort;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.sopt.makers.domain.official.soptstory.exception.SoptStoryException;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryScraperPort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoptStoryScraperAdapter implements SoptStoryScraperPort, ReviewScraperPort {

  private final CrawlerComposite crawlerComposite;

  @Override
  public ScrapedArticle scrap(String articleUrl) {
    try {
      LinkSource source = LinkSource.parseSource(articleUrl);
      return normalize(crawlerComposite.crawl(source, articleUrl), articleUrl);
    } catch (IOException e) {
      log.error("Scraping failed for URL: {}", articleUrl, e);
      throw new SoptStoryException(SCRAP_FAILED);
    }
  }

  @Override
  public ScrapedReviewArticle scrapReview(String articleUrl) {
    try {
      LinkSource source = LinkSource.parseSource(articleUrl);
      ScrapedArticle article = normalize(crawlerComposite.crawl(source, articleUrl), articleUrl);
      return new ScrapedReviewArticle(
          article.thumbnailUrl(),
          article.title(),
          article.description(),
          article.articleUrl(),
          article.platform());
    } catch (IOException e) {
      log.error("Review scraping failed for URL: {}", articleUrl, e);
      throw new ReviewException(ReviewFailure.SCRAP_FAILED);
    }
  }

  private ScrapedArticle normalize(ScrapedArticle article, String requestedUrl) {
    String articleUrl = defaultIfBlank(article.articleUrl(), requestedUrl);
    return new ScrapedArticle(
        defaultIfBlank(article.thumbnailUrl(), ""),
        defaultIfBlank(article.title(), articleUrl),
        defaultIfBlank(article.description(), ""),
        articleUrl,
        defaultIfBlank(article.platform(), ""));
  }

  private String defaultIfBlank(String value, String defaultValue) {
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
