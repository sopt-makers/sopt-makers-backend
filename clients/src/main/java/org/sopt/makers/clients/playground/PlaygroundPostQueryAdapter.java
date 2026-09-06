package org.sopt.makers.clients.playground;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.sopt.makers.clients.playground.dto.PlaygroundPopularPostDto;
import org.sopt.makers.clients.playground.dto.PlaygroundRecentPostDto;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.port.PlaygroundPostQueryPort;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// TODO: 플레이그라운드 게시글이 이 레포로 이관되면 HTTP 대신 같은 프로세스에서 읽도록 이 구현체를 갈아끼운다.
//  - 구서버와 같이 플레이그라운드 서버의 /internal/api/v1/community/posts/{latest,popular} 를 헤더 없이 부른다.
//  - 포트(domain-app PlaygroundPostQueryPort)와 홈 쪽 호출부는 그대로 두고 이 클래스만 바꾼다.
//  - 유저처럼 domain-playground 가 포트를 열고 domain-app 이 의존하려면 AGENTS.md 의존 허용 목록을 넓혀야 한다.
//    규칙을 안 바꾸면 storage 프로젝션 어댑터로 가야 하는데 카테고리별 최신글, 인기글 순위, 익명 프로필 로직이 복제된다.
//  - 그때 Redis 캐시, 매시 갱신 스케줄러, 갱신 이벤트 리스너를 계속 둘지 다시 본다. HTTP 호출을 줄이려던 장치다.
@Component
public class PlaygroundPostQueryAdapter implements PlaygroundPostQueryPort {

  private static final String RECENT_POSTS_PATH = "/internal/api/v1/community/posts/latest";
  private static final String POPULAR_POSTS_PATH = "/internal/api/v1/community/posts/popular";
  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

  private final RestClient restClient;

  public PlaygroundPostQueryAdapter(PlaygroundProperty playgroundProperty) {
    JdkClientHttpRequestFactory requestFactory =
        new JdkClientHttpRequestFactory(
            HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build());
    requestFactory.setReadTimeout(READ_TIMEOUT);
    this.restClient =
        RestClient.builder()
            .baseUrl(playgroundProperty.url())
            .requestFactory(requestFactory)
            .build();
  }

  @Override
  public List<PlaygroundRecentPost> getPlaygroundRecentPosts() {
    PlaygroundRecentPostDto[] response =
        restClient.get().uri(RECENT_POSTS_PATH).retrieve().body(PlaygroundRecentPostDto[].class);
    return Arrays.stream(requireBody(response)).map(PlaygroundRecentPostDto::toDomain).toList();
  }

  @Override
  public List<PlaygroundPopularPost> getPlaygroundPopularPosts() {
    PlaygroundPopularPostDto[] response =
        restClient.get().uri(POPULAR_POSTS_PATH).retrieve().body(PlaygroundPopularPostDto[].class);
    return Arrays.stream(requireBody(response)).map(PlaygroundPopularPostDto::toDomain).toList();
  }

  private static <T> T[] requireBody(T[] response) {
    if (response == null) {
      throw new RestClientException("플레이그라운드 게시글 응답 본문이 없습니다");
    }
    return response;
  }
}
