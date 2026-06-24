package org.sopt.makers.api.controller.internal.dto;

import org.sopt.makers.domain.official.scrap.port.ArticleScraperPort;

public class InternalScrapResponse {

  public record Scrap(String thumbnailUrl, String title, String description, String url) {
    public static Scrap of(ArticleScraperPort.ScrapedArticle article) {
      return new Scrap(
          article.thumbnailUrl(), article.title(), article.description(), article.articleUrl());
    }
  }
}
