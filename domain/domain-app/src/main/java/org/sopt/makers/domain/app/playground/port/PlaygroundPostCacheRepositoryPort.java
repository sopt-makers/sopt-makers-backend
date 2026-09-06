package org.sopt.makers.domain.app.playground.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;

public interface PlaygroundPostCacheRepositoryPort {

  Optional<List<PlaygroundRecentPost>> getCachedRecentPosts();

  Optional<List<PlaygroundPopularPost>> getCachedPopularPosts();

  void setCachedRecentPosts(List<PlaygroundRecentPost> posts);

  void setCachedPopularPosts(List<PlaygroundPopularPost> posts);

  boolean tryLockRecentRefresh();

  boolean tryLockPopularRefresh();
}
