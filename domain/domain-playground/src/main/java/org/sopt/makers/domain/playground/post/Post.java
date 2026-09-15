package org.sopt.makers.domain.playground.post;

import static org.sopt.makers.domain.playground.post.exception.PostFailure.FORBIDDEN_POST;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.INVALID_POST_CATEGORY;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.INVALID_POST_CONTENT_TYPE;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.TOO_MANY_POST_IMAGES;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.sopt.makers.domain.playground.post.exception.PostException;

public record Post(
    Long id,
    Long writerId,
    PostCategory category,
    PostContentType contentType,
    Long meetingId,
    String title,
    String contents,
    List<String> images,
    int viewCount,
    int commentCount,
    int likeCount,
    boolean isQuestion,
    boolean isAnonymous,
    boolean isReported,
    boolean isHot,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  private static final int MAX_IMAGE_COUNT = 10;

  public Post {
    images = images == null ? List.of() : List.copyOf(images);
    validateCategory(category, contentType, meetingId);
    validateImages(images);
  }

  public static Post createMeetingPost(
      Long writerId,
      Long meetingId,
      String title,
      String contents,
      List<String> images,
      PostContentType contentType) {
    return new Post(
        null,
        writerId,
        PostCategory.MEETING,
        contentType == null ? PostContentType.NORMAL : contentType,
        meetingId,
        title,
        contents,
        images,
        0,
        0,
        0,
        false,
        false,
        false,
        false,
        null,
        null);
  }

  public boolean isWriter(Long userId) {
    return writerId != null && Objects.equals(writerId, userId);
  }

  public void validateWriter(Long userId) {
    if (!isWriter(userId)) {
      throw new PostException(FORBIDDEN_POST);
    }
  }

  public Post update(String title, String contents, List<String> images) {
    validateImages(images);
    return copy(writerId, title, contents, images, viewCount, commentCount, likeCount);
  }

  public Post increaseViewCount() {
    return copy(writerId, title, contents, images, viewCount + 1, commentCount, likeCount);
  }

  public Post increaseCommentCount() {
    return copy(writerId, title, contents, images, viewCount, commentCount + 1, likeCount);
  }

  public Post increaseLikeCount() {
    return copy(writerId, title, contents, images, viewCount, commentCount, likeCount + 1);
  }

  public Post decreaseLikeCount() {
    return copy(
        writerId, title, contents, images, viewCount, commentCount, Math.max(0, likeCount - 1));
  }

  public Post anonymizeWriter() {
    return copy(null, title, contents, images, viewCount, commentCount, likeCount);
  }

  private Post copy(
      Long nextWriterId,
      String nextTitle,
      String nextContents,
      List<String> nextImages,
      int nextViewCount,
      int nextCommentCount,
      int nextLikeCount) {
    return new Post(
        id,
        nextWriterId,
        category,
        contentType,
        meetingId,
        nextTitle,
        nextContents,
        nextImages,
        nextViewCount,
        nextCommentCount,
        nextLikeCount,
        isQuestion,
        isAnonymous,
        isReported,
        isHot,
        createdAt,
        updatedAt);
  }

  private static void validateCategory(
      PostCategory category, PostContentType contentType, Long meetingId) {
    if (category != PostCategory.MEETING || meetingId == null) {
      throw new PostException(INVALID_POST_CATEGORY);
    }
    if (contentType == null) {
      throw new PostException(INVALID_POST_CONTENT_TYPE);
    }
  }

  private static void validateImages(List<String> images) {
    if (images != null && images.size() > MAX_IMAGE_COUNT) {
      throw new PostException(TOO_MANY_POST_IMAGES);
    }
  }
}
