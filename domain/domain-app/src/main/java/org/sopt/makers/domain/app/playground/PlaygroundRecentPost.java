package org.sopt.makers.domain.app.playground;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public record PlaygroundRecentPost(
    Long id,
    Long userId,
    String profileImage,
    String name,
    String generationAndPart,
    String category,
    String title,
    String content,
    String webLink,
    String createdAt,
    boolean isOutdated) {

  private static final DateTimeFormatter CREATED_AT_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
  private static final int OUTDATED_DAYS = 30;
  private static final String OUTDATED_TITLE = "아직 최신글이 없어요";
  private static final String DEFAULT_MESSAGE = "플레이그라운드에 새 글 올려봐!";
  private static final String UNKNOWN_IMAGE_KEY = "unknown.imageUrl";
  private static final Map<String, String> CATEGORY_MESSAGES =
      Map.of(
          "자유", "에 오늘의 TMI 적어봐!",
          "파트Talk", "에 파트 자랑해봐!",
          "질문", "에 진로 고민 공유해봐!",
          "홍보", "에 팀 서비스 소개해봐!",
          "솝티클", "에 직무 인사이트 써봐!");

  public PlaygroundRecentPost resolveOutdated(
      Map<String, String> imageConfigMap, LocalDateTime now) {
    if (!isOutdated(now)) {
      return new PlaygroundRecentPost(
          id,
          userId,
          profileImage,
          name,
          generationAndPart,
          category,
          title,
          content,
          webLink,
          createdAt,
          false);
    }
    String message = CATEGORY_MESSAGES.getOrDefault(category, DEFAULT_MESSAGE);
    String image =
        imageConfigMap.getOrDefault(category + ".imageUrl", imageConfigMap.get(UNKNOWN_IMAGE_KEY));
    return new PlaygroundRecentPost(
        null,
        null,
        image,
        null,
        null,
        category,
        OUTDATED_TITLE,
        message,
        trimWebLink(webLink),
        createdAt,
        true);
  }

  private boolean isOutdated(LocalDateTime now) {
    try {
      LocalDateTime created = LocalDateTime.parse(createdAt, CREATED_AT_FORMAT);
      return ChronoUnit.DAYS.between(created, now) > OUTDATED_DAYS;
    } catch (Exception e) {
      return false;
    }
  }

  private static String trimWebLink(String webLink) {
    if (webLink == null) {
      return null;
    }
    return webLink.split("\\?")[0];
  }
}
