package org.sopt.makers.clients.scrap;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.sopt.makers.clients.scrap.crawler.BasicLinkCrawler;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.springframework.stereotype.Component;

@Component
class CrawlerComposite {

  private final Map<LinkSource, LinkCrawler> mapping;

  CrawlerComposite(Set<LinkCrawler> crawlers) {
    this.mapping =
        crawlers.stream()
            .collect(Collectors.toUnmodifiableMap(LinkCrawler::supportSource, Function.identity()));
  }

  ScrapedArticle crawl(LinkSource source, String link) throws IOException {
    return Optional.ofNullable(mapping.get(source)).orElseGet(BasicLinkCrawler::new).crawl(link);
  }
}
