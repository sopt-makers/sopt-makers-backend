package org.sopt.makers.storage.redis.app.playground.cache;

import java.util.List;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;

public record CachedPlaygroundRecentPosts(List<CachedRecentPost> posts) {

  public static CachedPlaygroundRecentPosts from(List<PlaygroundRecentPost> posts) {
    return new CachedPlaygroundRecentPosts(posts.stream().map(CachedRecentPost::from).toList());
  }

  public List<PlaygroundRecentPost> toDomain() {
    return posts.stream().map(CachedRecentPost::toDomain).toList();
  }

  public record CachedRecentPost(
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

    static CachedRecentPost from(PlaygroundRecentPost post) {
      return new CachedRecentPost(
          post.id(),
          post.userId(),
          post.profileImage(),
          post.name(),
          post.generationAndPart(),
          post.category(),
          post.title(),
          post.content(),
          post.webLink(),
          post.createdAt(),
          post.isOutdated());
    }

    PlaygroundRecentPost toDomain() {
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
          isOutdated);
    }
  }
}
