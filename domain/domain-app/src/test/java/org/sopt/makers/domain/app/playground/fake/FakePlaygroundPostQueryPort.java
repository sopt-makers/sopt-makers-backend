package org.sopt.makers.domain.app.playground.fake;

import java.util.List;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.port.PlaygroundPostQueryPort;

public final class FakePlaygroundPostQueryPort implements PlaygroundPostQueryPort {

  private List<PlaygroundRecentPost> recent = List.of();
  private List<PlaygroundPopularPost> popular = List.of();
  private boolean failing;
  private int calls;

  public void setRecent(List<PlaygroundRecentPost> recent) {
    this.recent = recent;
  }

  public void setPopular(List<PlaygroundPopularPost> popular) {
    this.popular = popular;
  }

  public void fail() {
    failing = true;
  }

  public int calls() {
    return calls;
  }

  @Override
  public List<PlaygroundRecentPost> getPlaygroundRecentPosts() {
    calls++;
    if (failing) {
      throw new IllegalStateException("playground down");
    }
    return recent;
  }

  @Override
  public List<PlaygroundPopularPost> getPlaygroundPopularPosts() {
    calls++;
    if (failing) {
      throw new IllegalStateException("playground down");
    }
    return popular;
  }
}
