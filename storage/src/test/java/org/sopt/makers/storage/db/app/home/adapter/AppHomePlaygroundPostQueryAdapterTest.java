package org.sopt.makers.storage.db.app.home.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.exception.CommunityFailure;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService.InternalPopularPostSummary;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService.InternalPostSummary;

@DisplayName("AppHomePlaygroundPostQueryAdapter 테스트")
class AppHomePlaygroundPostQueryAdapterTest {

  private CommunityPostQueryService communityPostQueryService;
  private AppHomePlaygroundPostQueryAdapter adapter;

  @BeforeEach
  void setUp() {
    communityPostQueryService = mock(CommunityPostQueryService.class);
    adapter = new AppHomePlaygroundPostQueryAdapter(communityPostQueryService);
  }

  @Test
  @DisplayName("최신 게시글은 domain-playground를 직접 호출해 id와 createdAt까지 매핑한다")
  void fetchesRecentPosts() {
    InternalPostSummary summary =
        new InternalPostSummary(
            11L,
            7L,
            "https://img/p.png",
            "홍길동",
            "38기 서버",
            "자유",
            "제목",
            "내용",
            "https://pg/11",
            LocalDateTime.of(2026, 9, 1, 10, 0, 0));
    when(communityPostQueryService.getInternalLatestPosts()).thenReturn(List.of(summary));

    List<PlaygroundRecentPost> posts = adapter.getPlaygroundRecentPosts();

    assertThat(posts).hasSize(1);
    assertThat(posts.getFirst().id()).isEqualTo(11L);
    assertThat(posts.getFirst().createdAt()).isEqualTo("2026-09-01 10:00:00.000000");
    assertThat(posts.getFirst().isOutdated()).isFalse();
  }

  @Test
  @DisplayName("인기 게시글은 rank까지 매핑하고 고정 limit(3)으로 조회한다")
  void fetchesPopularPosts() {
    InternalPopularPostSummary summary =
        new InternalPopularPostSummary(
            21L, 8L, null, "김솝트", "38기 웹", 1, "질문", "인기글", "본문", "https://pg/21");
    when(communityPostQueryService.getInternalPopularPosts(3)).thenReturn(List.of(summary));

    List<PlaygroundPopularPost> posts = adapter.getPlaygroundPopularPosts();

    assertThat(posts)
        .containsExactly(
            new PlaygroundPopularPost(
                21L, 8L, null, "김솝트", "38기 웹", 1, "질문", "인기글", "본문", "https://pg/21"));
  }

  @Test
  @DisplayName("인기 게시글이 없으면 domain-playground의 예외를 그대로 전파해 캐시가 갱신되지 않게 한다")
  void emptyPopularPostsPropagatesException() {
    when(communityPostQueryService.getInternalPopularPosts(3))
        .thenThrow(new CommunityException(CommunityFailure.NO_RECENT_POPULAR_POSTS));

    assertThatThrownBy(adapter::getPlaygroundPopularPosts).isInstanceOf(CommunityException.class);
  }
}
