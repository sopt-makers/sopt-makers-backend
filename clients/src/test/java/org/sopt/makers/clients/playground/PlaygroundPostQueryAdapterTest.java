package org.sopt.makers.clients.playground;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.springframework.web.client.RestClientException;

@DisplayName("PlaygroundPostQueryAdapter 테스트")
class PlaygroundPostQueryAdapterTest {

  private MockWebServer playgroundServer;
  private PlaygroundPostQueryAdapter adapter;

  @BeforeEach
  void setUp() throws Exception {
    playgroundServer = new MockWebServer();
    playgroundServer.start();
    String baseUrl = playgroundServer.url("/").toString().replaceAll("/$", "");
    adapter = new PlaygroundPostQueryAdapter(new PlaygroundProperty(baseUrl, null));
  }

  @AfterEach
  void tearDown() throws Exception {
    playgroundServer.shutdown();
  }

  @Test
  @DisplayName("최신 게시글은 헤더 없이 GET 하고 id 와 createdAt 까지 매핑한다")
  void fetchesRecentPosts() throws Exception {
    playgroundServer.enqueue(
        json(
            """
            [{"id": 11, "userId": 7, "profileImage": "https://img/p.png", "name": "홍길동",
              "generationAndPart": "38기 서버", "category": "자유", "title": "제목", "content": "내용",
              "webLink": "https://pg/11", "createdAt": "2026-09-01 10:00:00.000000", "extra": 1}]
            """));

    List<PlaygroundRecentPost> posts = adapter.getPlaygroundRecentPosts();

    RecordedRequest request = playgroundServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getPath()).isEqualTo("/internal/api/v1/community/posts/latest");
    assertThat(request.getHeader("Authorization")).isNull();
    assertThat(posts).hasSize(1);
    assertThat(posts.getFirst().id()).isEqualTo(11L);
    assertThat(posts.getFirst().createdAt()).isEqualTo("2026-09-01 10:00:00.000000");
    assertThat(posts.getFirst().isOutdated()).isFalse();
  }

  @Test
  @DisplayName("인기 게시글은 rank 까지 매핑한다")
  void fetchesPopularPosts() throws Exception {
    playgroundServer.enqueue(
        json(
            """
            [{"id": 21, "userId": 8, "profileImage": null, "name": "김솝트", "generationAndPart": "38기 웹", "rank": 1,
              "category": "질문", "title": "인기글", "content": "본문", "webLink": "https://pg/21"}]
            """));

    List<PlaygroundPopularPost> posts = adapter.getPlaygroundPopularPosts();

    assertThat(playgroundServer.takeRequest().getPath())
        .isEqualTo("/internal/api/v1/community/posts/popular");
    assertThat(posts)
        .containsExactly(
            new PlaygroundPopularPost(
                21L, 8L, null, "김솝트", "38기 웹", 1, "질문", "인기글", "본문", "https://pg/21"));
  }

  @Test
  @DisplayName("본문이 비면 예외를 던져 캐시가 갱신되지 않게 한다")
  void emptyBodyThrows() {
    playgroundServer.enqueue(new MockResponse().setResponseCode(200));

    assertThatThrownBy(adapter::getPlaygroundRecentPosts).isInstanceOf(RestClientException.class);
  }

  private static MockResponse json(String body) {
    return new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(body);
  }
}
