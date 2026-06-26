package org.sopt.makers.api.controller.official.soptstory.dto;

import org.sopt.makers.domain.official.soptstory.ScrapedArticle;

public record CreateSoptStoryResponse(
    String thumbnailUrl, String title, String description, String articleUrl) {

  public static CreateSoptStoryResponse from(ScrapedArticle article) {
    return new CreateSoptStoryResponse(
        article.thumbnailUrl(), article.title(), article.description(), article.articleUrl());
  }
}
