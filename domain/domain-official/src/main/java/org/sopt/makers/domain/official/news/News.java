package org.sopt.makers.domain.official.news;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record News(
    Integer id,
    String imageUrl,
    String title,
    String link,
    Instant publishedAt,
    boolean hidden,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public String publishedDate() {
    return publishedAt == null
        ? null
        : publishedAt
            .atZone(ZoneId.of("Asia/Seoul"))
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
  }

  public static News create(String imageUrl, String title, String link) {
    return new News(null, imageUrl, title, link, null, false, null, null);
  }

  public News update(String imageUrl, String title, String link) {
    return new News(id, imageUrl, title, link, publishedAt, hidden, createdAt, updatedAt);
  }
}
