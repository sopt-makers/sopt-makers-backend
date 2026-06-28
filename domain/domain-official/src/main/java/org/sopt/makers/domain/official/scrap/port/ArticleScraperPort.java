package org.sopt.makers.domain.official.scrap.port;

public interface ArticleScraperPort {
  ScrapedArticle scrap(String url);

  record ScrapedArticle(String thumbnailUrl, String title, String description, String articleUrl) {}
}
