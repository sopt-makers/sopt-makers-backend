package org.sopt.makers.clients.scraper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.sopt.makers.clients.scraper.crawler.LinkCrawler;
import org.sopt.makers.clients.scraper.crawler.LinkSource;
import org.sopt.makers.domain.official.scrap.exception.ScrapException;
import org.sopt.makers.domain.official.scrap.exception.ScrapFailure;
import org.sopt.makers.domain.official.scrap.port.ArticleScraperPort;
import org.springframework.stereotype.Component;

@Component
public class ArticleScraperAdapter implements ArticleScraperPort {

  private final Map<LinkSource, LinkCrawler> crawlers;

  public ArticleScraperAdapter(Set<LinkCrawler> crawlerSet) {
    this.crawlers =
        crawlerSet.stream()
            .collect(Collectors.toUnmodifiableMap(LinkCrawler::supportSource, Function.identity()));
  }

  @Override
  public ScrapedArticle scrap(String url) {
    LinkSource source = LinkSource.parseSource(url);
    LinkCrawler crawler = crawlers.getOrDefault(source, crawlers.get(LinkSource.BASIC));
    try {
      LinkCrawler.ScraperResult result = crawler.crawl(url);
      String articleUrl = result.articleUrl().isBlank() ? url : result.articleUrl();
      return new ScrapedArticle(result.thumbnailUrl(), result.title(), result.description(), articleUrl);
    } catch (IOException e) {
      throw new ScrapException(ScrapFailure.SCRAPING_FAILED);
    }
  }
}
