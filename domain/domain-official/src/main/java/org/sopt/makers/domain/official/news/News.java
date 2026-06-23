package org.sopt.makers.domain.official.news;

import java.time.LocalDateTime;

public record News(
    Integer id,
    String imageUrl,
    String title,
    String link,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static News create(String imageUrl, String title, String link) {
    return new News(null, imageUrl, title, link, null, null);
  }

  public News update(String imageUrl, String title, String link) {
    return new News(id, imageUrl, title, link, createdAt, updatedAt);
  }
}
