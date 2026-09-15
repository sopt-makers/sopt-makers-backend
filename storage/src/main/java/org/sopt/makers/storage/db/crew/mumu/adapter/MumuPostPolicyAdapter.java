package org.sopt.makers.storage.db.crew.mumu.adapter;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.mumu.MumuPostWriteHistory;
import org.sopt.makers.domain.crew.mumu.port.MumuPostWriteHistoryRepositoryPort;
import org.sopt.makers.domain.crew.mumu.service.MumuTextService;
import org.sopt.makers.domain.playground.post.port.MumuPostPolicyPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MumuPostPolicyAdapter implements MumuPostPolicyPort {

  private final MumuTextService mumuTextService;
  private final MumuPostWriteHistoryRepositoryPort writeHistoryRepositoryPort;

  @Override
  public String getCurrentText() {
    return mumuTextService.getCurrentText();
  }

  @Override
  public boolean hasWritten(Long userId, LocalDate writtenDate) {
    return writeHistoryRepositoryPort.existsByUserIdAndWrittenDate(userId, writtenDate);
  }

  @Override
  public void recordWrittenIfAbsent(Long userId, LocalDate writtenDate) {
    if (!hasWritten(userId, writtenDate)) {
      writeHistoryRepositoryPort.save(MumuPostWriteHistory.create(userId, writtenDate));
    }
  }
}
