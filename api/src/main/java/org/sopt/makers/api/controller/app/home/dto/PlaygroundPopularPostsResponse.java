package org.sopt.makers.api.controller.app.home.dto;

import java.util.List;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;

public record PlaygroundPopularPostsResponse(List<PopularPost> popularPosts) {

  public static PlaygroundPopularPostsResponse of(List<PlaygroundPopularPost> posts) {
    return new PlaygroundPopularPostsResponse(posts.stream().map(PopularPost::of).toList());
  }

  public record PopularPost(
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

    private static PopularPost of(PlaygroundPopularPost post) {
      return new PopularPost(
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
  }
}
