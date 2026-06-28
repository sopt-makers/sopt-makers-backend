package org.sopt.makers.clients.scrap;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.official.scrap.exception.ScrapException;
import org.sopt.makers.domain.official.scrap.exception.ScrapFailure;
import org.sopt.makers.domain.official.scrap.port.ArticleScraperPort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleScraperAdapter implements ArticleScraperPort {

  private final CrawlerComposite crawlerComposite;

  @Override
  public ArticleScraperPort.ScrapedArticle scrap(String url) {
    try {
      LinkSource source = LinkSource.parseSource(url);
      var result = crawlerComposite.crawl(source, url);
      String articleUrl =
          (result.articleUrl() == null || result.articleUrl().isBlank())
              ? url
              : result.articleUrl();
      return new ArticleScraperPort.ScrapedArticle(
          result.thumbnailUrl(), result.title(), result.description(), articleUrl);
    } catch (IOException e) {
      log.error("Article scraping failed for URL: {}", url, e);
      throw new ScrapException(ScrapFailure.SCRAPING_FAILED);
    }
  }
}
