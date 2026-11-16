package org.sopt.makers.domain.official.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.domain.official.news.port.*;
import org.springframework.test.util.ReflectionTestUtils;

class NewsServiceTest {
  private final NewsRepositoryPort port = mock(NewsRepositoryPort.class);
  private final NewsFileStoragePort files = mock(NewsFileStoragePort.class);
  private final NewsService service = new NewsService(port, files);

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

  @Test
  @DisplayName("이미지 URL을 바꾸면 이전 파일을 삭제하고 소식 정보를 갱신한다")
  void replacesImageUrl() {
    var original = news(1, Instant.parse("2026-01-01T00:00:00Z"), true);
    when(port.findById(1)).thenReturn(Optional.of(original));
    when(port.save(any())).thenAnswer(inv -> inv.getArgument(0));
    var result = service.edit(1, new NewsService.EditNewsCommand("new-image", "수정", "new-link"));
    verify(files).delete("image");
    assertThat(result.imageUrl()).isEqualTo("new-image");
    assertThat(result.title()).isEqualTo("수정");
    assertThat(result.link()).isEqualTo("new-link");
    assertThat(result.publishedAt()).isEqualTo(original.publishedAt());
    assertThat(result.hidden()).isTrue();
  }

  @Test
  @DisplayName("이미지 URL이 같으면 파일을 삭제하지 않는다")
  void keepsSameImageUrl() {
    when(port.findById(1)).thenReturn(Optional.of(news(1, null, false)));
    when(port.save(any())).thenAnswer(inv -> inv.getArgument(0));
    service.edit(1, new NewsService.EditNewsCommand("image", "수정", "new-link"));
    verifyNoInteractions(files);
  }

  @Test
  @DisplayName("새 파일 없이 수정하면 기존 이미지 주소를 유지한다")
  void keepsImageWithoutNewFile() {
    when(port.findById(1)).thenReturn(Optional.of(news(1, null, false)));
    when(port.save(any())).thenAnswer(inv -> inv.getArgument(0));
    var result =
        service.editWithFile(1, new NewsService.EditNewsWithFileCommand(null, "수정", "new-link"));
    assertThat(result.imageUrl()).isEqualTo("image");
    verifyNoInteractions(files);
  }

  @Test
  @DisplayName("새 파일로 수정하면 이전 파일을 삭제하고 업로드한 주소를 저장한다")
  void replacesImageFile() {
    var file =
        new NewsFileStoragePort.UploadFile(
            "new.png", "image/png", 1, new java.io.ByteArrayInputStream(new byte[] {1}));
    when(port.findById(1)).thenReturn(Optional.of(news(1, null, false)));
    when(files.upload(file, "news/")).thenReturn("new-image");
    when(port.save(any())).thenAnswer(inv -> inv.getArgument(0));
    var result =
        service.editWithFile(1, new NewsService.EditNewsWithFileCommand(file, "수정", "new-link"));
    verify(files).delete("image");
    verify(files).upload(file, "news/");
    assertThat(result.imageUrl()).isEqualTo("new-image");
  }
}
