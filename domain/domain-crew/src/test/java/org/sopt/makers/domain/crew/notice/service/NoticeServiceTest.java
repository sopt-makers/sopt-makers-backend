package org.sopt.makers.domain.crew.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.notice.Notice;
import org.sopt.makers.domain.crew.notice.exception.NoticeException;
import org.sopt.makers.domain.crew.notice.port.NoticeAuthorizerPort;
import org.sopt.makers.domain.crew.notice.port.NoticeRepositoryPort;

class NoticeServiceTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-08-27T00:00:00Z"), ZoneId.of("Asia/Seoul"));

  private final NoticeRepositoryPort repository = mock(NoticeRepositoryPort.class);
  private final NoticeAuthorizerPort authorizer = mock(NoticeAuthorizerPort.class);
  private final NoticeService service = new NoticeService(repository, authorizer, CLOCK);

  @Test
  @DisplayName("현재 시각에 노출 중인 공지를 조회한다")
  void getsCurrentlyExposedNotices() {
    service.getNotices();

    verify(repository).findExposedAt(LocalDateTime.now(CLOCK));
  }

  @Test
  @DisplayName("유효한 secret key로 공지를 생성한다")
  void createsNoticeWithValidSecret() {
    NoticeService.CreateNoticeCommand command = command("secret");
    when(authorizer.isAuthorized("secret")).thenReturn(true);
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Notice result = service.createNotice(command);

    assertThat(result.title()).isEqualTo("공지 제목");
    assertThat(result.createdDate()).isEqualTo(LocalDateTime.now(CLOCK));
  }

  @Test
  @DisplayName("유효하지 않은 secret key로 공지를 생성할 수 없다")
  void rejectsInvalidSecret() {
    when(authorizer.isAuthorized("invalid")).thenReturn(false);

    assertThatThrownBy(() -> service.createNotice(command("invalid")))
        .isInstanceOf(NoticeException.class);
  }

  private NoticeService.CreateNoticeCommand command(String secretKey) {
    return new NoticeService.CreateNoticeCommand(
        "공지 제목",
        "공지 부제목",
        "공지 내용",
        LocalDateTime.of(2026, 8, 27, 0, 0),
        LocalDateTime.of(2026, 8, 31, 23, 59),
        secretKey);
  }
}
