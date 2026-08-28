package org.sopt.makers.domain.crew.notice.service;

import static org.sopt.makers.domain.crew.notice.exception.NoticeFailure.FORBIDDEN_NOTICE_CREATION;
import static org.sopt.makers.domain.crew.notice.exception.NoticeFailure.INVALID_NOTICE_EXPOSURE_PERIOD;
import static org.sopt.makers.domain.crew.notice.exception.NoticeFailure.INVALID_NOTICE_VALUE;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.notice.Notice;
import org.sopt.makers.domain.crew.notice.exception.NoticeException;
import org.sopt.makers.domain.crew.notice.port.NoticeAuthorizerPort;
import org.sopt.makers.domain.crew.notice.port.NoticeRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

  private final NoticeRepositoryPort noticeRepositoryPort;
  private final NoticeAuthorizerPort noticeAuthorizerPort;
  private final Clock clock;

  public List<Notice> getNotices() {
    return noticeRepositoryPort.findExposedAt(LocalDateTime.now(clock));
  }

  @Transactional
  public Notice createNotice(CreateNoticeCommand command) {
    if (!noticeAuthorizerPort.isAuthorized(command.noticeSecretKey())) {
      throw new NoticeException(FORBIDDEN_NOTICE_CREATION);
    }
    validate(command);
    return noticeRepositoryPort.save(
        Notice.create(
            command.title(),
            command.subTitle(),
            command.contents(),
            LocalDateTime.now(clock),
            command.exposeStartDate(),
            command.exposeEndDate()));
  }

  private void validate(CreateNoticeCommand command) {
    if (command.title() == null
        || command.title().isBlank()
        || command.subTitle() == null
        || command.subTitle().isBlank()
        || command.contents() == null
        || command.contents().isBlank()
        || command.exposeStartDate() == null
        || command.exposeEndDate() == null) {
      throw new NoticeException(INVALID_NOTICE_VALUE);
    }
    if (command.exposeEndDate().isBefore(command.exposeStartDate())) {
      throw new NoticeException(INVALID_NOTICE_EXPOSURE_PERIOD);
    }
  }

  public record CreateNoticeCommand(
      String title,
      String subTitle,
      String contents,
      LocalDateTime exposeStartDate,
      LocalDateTime exposeEndDate,
      String noticeSecretKey) {}
}
