package org.sopt.makers.domain.app.playground.port;

import java.util.List;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;

public interface AppHomePlaygroundPostQueryPort {

  List<PlaygroundRecentPost> getPlaygroundRecentPosts();

  List<PlaygroundPopularPost> getPlaygroundPopularPosts();
}
