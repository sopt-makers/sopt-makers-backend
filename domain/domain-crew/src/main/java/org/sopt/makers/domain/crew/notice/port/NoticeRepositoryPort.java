package org.sopt.makers.domain.crew.notice.port;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.notice.Notice;

public interface NoticeRepositoryPort {

  Notice save(Notice notice);

  List<Notice> findExposedAt(LocalDateTime now);
}
