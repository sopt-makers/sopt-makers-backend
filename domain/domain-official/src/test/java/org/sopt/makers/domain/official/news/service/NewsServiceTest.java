package org.sopt.makers.domain.official.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.domain.official.news.port.*;
import org.springframework.test.util.ReflectionTestUtils;

class NewsServiceTest {
  private final NewsRepositoryPort port = mock(NewsRepositoryPort.class);
  private final NewsService service = new NewsService(port, mock(NewsFileStoragePort.class));

  @Test
  @DisplayName("게시일과 생성일 및 ID 내림차순으로 공개 소식을 조회한다")
  void sortsVisibleNews() {
    var old = Instant.parse("2026-01-01T00:00:00Z");
    var recent = old.plusSeconds(86400);
    when(port.findAll())
        .thenReturn(
            List.of(
                news(1, old, false),
                news(3, recent, false),
                news(2, recent, false),
                news(4, null, false),
                news(5, recent, true)));
    ReflectionTestUtils.setField(service, "displayLimit", 3);
    assertThat(service.findLatestForHomepage()).extracting(News::id).containsExactly(3, 2, 1);
  }

  @Test
  @DisplayName("표시 개수가 0이면 공개 소식을 모두 조회한다")
  void unlimitedNews() {
    when(port.findAll()).thenReturn(List.of(news(1, null, false), news(2, null, false)));
    ReflectionTestUtils.setField(service, "displayLimit", 0);
    assertThat(service.findLatestForHomepage()).hasSize(2);
  }

  @Test
  @DisplayName("게시일은 한국 시간 기준으로 표시하며 게시일이 없으면 null이다")
  void publishedDate() {
    assertThat(news(1, Instant.parse("2026-01-01T16:00:00Z"), false).publishedDate())
        .isEqualTo("2026.01.02");
    assertThat(news(2, null, false).publishedDate()).isNull();
  }

  private News news(int id, Instant time, boolean hidden) {
    return new News(
        id, "image", "소식", "link", time, hidden, LocalDateTime.of(2026, 1, 1, 0, 0), null);
  }
}
