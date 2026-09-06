package org.sopt.makers.api.controller.app.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;

public record PlaygroundRecentPostsResponse(List<RecentPost> recentPosts) {

  public static PlaygroundRecentPostsResponse of(List<PlaygroundRecentPost> posts) {
    return new PlaygroundRecentPostsResponse(posts.stream().map(RecentPost::of).toList());
  }

  public record RecentPost(
      Long id,
      Long userId,
      String profileImage,
      String name,
      String generationAndPart,
      String category,
      String title,
      String content,
      String webLink,
      @JsonProperty("isOutdated") boolean isOutdated) {

    private static RecentPost of(PlaygroundRecentPost post) {
      return new RecentPost(
          post.id(),
          post.userId(),
          post.profileImage(),
          post.name(),
          post.generationAndPart(),
          post.category(),
          post.title(),
          post.content(),
          post.webLink(),
          post.isOutdated());
    }
  }
}
