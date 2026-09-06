package org.sopt.makers.domain.crew.mumu.port;

import java.time.LocalDate;
import org.sopt.makers.domain.crew.mumu.MumuPostWriteHistory;

public interface MumuPostWriteHistoryRepositoryPort {

  MumuPostWriteHistory save(MumuPostWriteHistory history);

  boolean existsByUserIdAndWrittenDate(Long userId, LocalDate writtenDate);
}
