package org.sopt.makers.api.controller.official.homepage.news.dto;

import java.util.List;
import org.sopt.makers.domain.official.homepage.news.News;

public record NewsResponse(Integer id, String imageUrl, String title, String link) {

  public static NewsResponse of(News news) {
    return new NewsResponse(news.id(), news.imageUrl(), news.title(), news.link());
  }

  public static List<NewsResponse> of(List<News> news) {
    return news.stream().map(NewsResponse::of).toList();
  }
}
