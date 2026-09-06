package org.sopt.makers.domain.app.playground;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PlaygroundRecentPost 테스트")
class PlaygroundRecentPostTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 3, 12, 0);
  private static final Map<String, String> IMAGE_CONFIG =
      Map.of("자유.imageUrl", "https://img/free.png", "unknown.imageUrl", "https://img/unknown.png");

  @Test
  @DisplayName("30일 안의 글은 그대로 두고 isOutdated 는 false 다")
  void keepsRecentPost() {
    PlaygroundRecentPost post = postOf("자유", "2026-08-20 10:00:00.000000", "https://pg/1?ref=x");

    PlaygroundRecentPost resolved = post.resolveOutdated(IMAGE_CONFIG, NOW);

    assertThat(resolved.isOutdated()).isFalse();
    assertThat(resolved.id()).isEqualTo(1L);
    assertThat(resolved.title()).isEqualTo("제목");
    assertThat(resolved.webLink()).isEqualTo("https://pg/1?ref=x");
  }

  @Test
  @DisplayName("30일 넘은 글은 안내 문구, 카테고리 이미지, 쿼리 없는 링크로 바뀐다")
  void replacesOutdatedPost() {
    PlaygroundRecentPost post = postOf("자유", "2026-07-01 10:00:00.000000", "https://pg/1?ref=x");

    PlaygroundRecentPost resolved = post.resolveOutdated(IMAGE_CONFIG, NOW);

    assertThat(resolved.isOutdated()).isTrue();
    assertThat(resolved.id()).isNull();
    assertThat(resolved.userId()).isNull();
    assertThat(resolved.name()).isNull();
    assertThat(resolved.generationAndPart()).isNull();
    assertThat(resolved.title()).isEqualTo("아직 최신글이 없어요");
    assertThat(resolved.content()).isEqualTo("에 오늘의 TMI 적어봐!");
    assertThat(resolved.profileImage()).isEqualTo("https://img/free.png");
    assertThat(resolved.webLink()).isEqualTo("https://pg/1");
  }

  @Test
  @DisplayName("모르는 카테고리는 unknown 이미지와 기본 문구를 쓴다")
  void fallsBackForUnknownCategory() {
    PlaygroundRecentPost post = postOf("기타", "2026-07-01 10:00:00.000000", null);

    PlaygroundRecentPost resolved = post.resolveOutdated(IMAGE_CONFIG, NOW);

    assertThat(resolved.profileImage()).isEqualTo("https://img/unknown.png");
    assertThat(resolved.content()).isEqualTo("플레이그라운드에 새 글 올려봐!");
    assertThat(resolved.webLink()).isNull();
  }

  @Test
  @DisplayName("createdAt 을 파싱하지 못하면 outdated 로 보지 않는다")
  void treatsUnparsableCreatedAtAsRecent() {
    PlaygroundRecentPost post = postOf("자유", "not-a-date", "https://pg/1");

    assertThat(post.resolveOutdated(IMAGE_CONFIG, NOW).isOutdated()).isFalse();
  }

  private static PlaygroundRecentPost postOf(String category, String createdAt, String webLink) {
    return new PlaygroundRecentPost(
        1L,
        10L,
        "https://img/p.png",
        "홍길동",
        "38기 서버",
        category,
        "제목",
        "내용",
        webLink,
        createdAt,
        false);
  }
}
