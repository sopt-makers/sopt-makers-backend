package org.sopt.makers.domain.playground.post.port;

import java.time.LocalDate;
import org.sopt.makers.domain.playground.post.mumu.MumuPostWriteHistory;

public interface MumuPostWriteHistoryRepositoryPort {

  MumuPostWriteHistory save(MumuPostWriteHistory history);

  boolean existsByUserIdAndWrittenDate(Long userId, LocalDate writtenDate);
}
