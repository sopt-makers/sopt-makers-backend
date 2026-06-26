package org.sopt.makers.domain.official.review;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.review.exception.ReviewException;
import org.sopt.makers.domain.official.review.exception.ReviewFailure;

public record Review(
    Long id,
    String title,
    String description,
    String thumbnailUrl,
    String platform,
    String author,
    String authorProfileImageUrl,
    Integer generation,
    Part part,
    CategoryType category,
    List<String> subjects,
    String url,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static Review create(
      String title,
      String description,
      String thumbnailUrl,
      String platform,
      String author,
      String authorProfileImageUrl,
      Integer generation,
      Part part,
      CategoryType category,
      List<String> subjects,
      String url) {
    List<String> safeSubjects = subjects == null ? List.of() : List.copyOf(subjects);
    validate(title, description, thumbnailUrl, platform, author, authorProfileImageUrl, generation, part, category, safeSubjects, url);
    return new Review(
        null,
        title,
        description,
        thumbnailUrl,
        platform,
        author,
        authorProfileImageUrl,
        generation,
        part,
        category,
        safeSubjects,
        url,
        null,
        null);
  }

  private static void validate(
      String title,
      String description,
      String thumbnailUrl,
      String platform,
      String author,
      String authorProfileImageUrl,
      Integer generation,
      Part part,
      CategoryType category,
      List<String> subjects,
      String url) {
    validateContent(title, description, thumbnailUrl, platform);
    validateAuthor(author, authorProfileImageUrl);
    validateGeneration(generation);
    validatePart(part);
    validateCategory(category, subjects);
    validateUrl(url);
  }

  private static void validateContent(
      String title, String description, String thumbnailUrl, String platform) {
    if (title == null || title.isBlank() || title.length() > 1000) {
      throw new ReviewException(ReviewFailure.INVALID_CONTENT);
    }
    if (description == null || description.isBlank() || description.length() > 2000) {
      throw new ReviewException(ReviewFailure.INVALID_CONTENT);
    }
    if (platform == null || platform.isBlank() || platform.length() > 50) {
      throw new ReviewException(ReviewFailure.INVALID_CONTENT);
    }
    if (thumbnailUrl != null && thumbnailUrl.length() > 500) {
      throw new ReviewException(ReviewFailure.INVALID_CONTENT);
    }
  }

  private static void validateAuthor(String author, String authorProfileImageUrl) {
    if (author == null || author.isBlank() || author.length() > 20) {
      throw new ReviewException(ReviewFailure.INVALID_AUTHOR);
    }
    if (authorProfileImageUrl != null && authorProfileImageUrl.length() > 500) {
      throw new ReviewException(ReviewFailure.INVALID_AUTHOR);
    }
  }

  private static void validateGeneration(Integer generation) {
    if (generation == null || generation <= 0) {
      throw new ReviewException(ReviewFailure.INVALID_GENERATION);
    }
  }

  private static void validatePart(Part part) {
    if (part == null) {
      throw new ReviewException(ReviewFailure.INVALID_PART);
    }
  }

  private static void validateCategory(CategoryType category, List<String> subjects) {
    if (category == null) {
      throw new ReviewException(ReviewFailure.INVALID_CATEGORY);
    }
    if ((category.isRequiresSubActivities() || category.isRecruiting()) && subjects.isEmpty()) {
      throw new ReviewException(ReviewFailure.INVALID_SUBJECT);
    }
  }

  private static void validateUrl(String url) {
    if (url == null
        || url.isBlank()
        || url.length() > 500
        || (!url.startsWith("http://") && !url.startsWith("https://"))) {
      throw new ReviewException(ReviewFailure.INVALID_URL);
    }
  }
}
