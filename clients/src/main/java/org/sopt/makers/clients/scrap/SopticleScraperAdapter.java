package org.sopt.makers.clients.scrap;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.exception.CommunityFailure;
import org.sopt.makers.domain.playground.community.post.port.SopticleScraperPort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SopticleScraperAdapter implements SopticleScraperPort {

  private final CrawlerComposite crawlerComposite;

  @Override
  public ScrapedSopticleArticle scrap(String url) {
    try {
      LinkSource source = LinkSource.parseSource(url);
      ScrapedArticle article = crawlerComposite.crawl(source, url);
      String articleUrl = article.articleUrl() == null || article.articleUrl().isBlank() ? url : article.articleUrl();
      return new ScrapedSopticleArticle(article.thumbnailUrl(), article.title(), article.description(), articleUrl);
    } catch (IOException e) {
      log.error("Sopticle scraping failed for URL: {}", url, e);
      throw new CommunityException(CommunityFailure.SOPTICLE_SCRAP_FAILED);
    }
  }
}
