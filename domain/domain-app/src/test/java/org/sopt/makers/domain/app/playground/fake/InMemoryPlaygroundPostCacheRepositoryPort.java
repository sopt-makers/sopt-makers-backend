package org.sopt.makers.domain.app.playground.fake;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.port.PlaygroundPostCacheRepositoryPort;

public final class InMemoryPlaygroundPostCacheRepositoryPort
    implements PlaygroundPostCacheRepositoryPort {

  private List<PlaygroundRecentPost> recent;
  private List<PlaygroundPopularPost> popular;
  private boolean recentLocked;
  private boolean popularLocked;

  public void lockRecent() {
    recentLocked = true;
  }

  public void lockPopular() {
    popularLocked = true;
  }

  @Override
  public Optional<List<PlaygroundRecentPost>> getCachedRecentPosts() {
    return Optional.ofNullable(recent);
  }

  @Override
  public Optional<List<PlaygroundPopularPost>> getCachedPopularPosts() {
    return Optional.ofNullable(popular);
  }

  @Override
  public void setCachedRecentPosts(List<PlaygroundRecentPost> posts) {
    recent = posts;
  }

  @Override
  public void setCachedPopularPosts(List<PlaygroundPopularPost> posts) {
    popular = posts;
  }

  @Override
  public boolean tryLockRecentRefresh() {
    if (recentLocked) {
      return false;
    }
    recentLocked = true;
    return true;
  }

  @Override
  public boolean tryLockPopularRefresh() {
    if (popularLocked) {
      return false;
    }
    popularLocked = true;
    return true;
  }
}
