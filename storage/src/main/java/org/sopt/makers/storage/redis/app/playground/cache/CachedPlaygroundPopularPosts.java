package org.sopt.makers.storage.redis.app.playground.cache;

import java.util.List;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;

public record CachedPlaygroundPopularPosts(List<CachedPopularPost> posts) {

  public static CachedPlaygroundPopularPosts from(List<PlaygroundPopularPost> posts) {
    return new CachedPlaygroundPopularPosts(posts.stream().map(CachedPopularPost::from).toList());
  }

  public List<PlaygroundPopularPost> toDomain() {
    return posts.stream().map(CachedPopularPost::toDomain).toList();
  }

  public record CachedPopularPost(
      Long id,
      Long userId,
      String profileImage,
      String name,
      String generationAndPart,
      int rank,
      String category,
      String title,
      String content,
      String webLink) {

    static CachedPopularPost from(PlaygroundPopularPost post) {
      return new CachedPopularPost(
          post.id(),
          post.userId(),
          post.profileImage(),
          post.name(),
          post.generationAndPart(),
          post.rank(),
          post.category(),
          post.title(),
          post.content(),
          post.webLink());
    }

    PlaygroundPopularPost toDomain() {
      return new PlaygroundPopularPost(
          id,
          userId,
          profileImage,
          name,
          generationAndPart,
          rank,
          category,
          title,
          content,
          webLink);
    }
  }
}
