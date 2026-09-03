package org.sopt.makers.storage.db.playground.post.adapter;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.mumu.MumuPostWriteHistory;
import org.sopt.makers.domain.playground.post.port.MumuPostWriteHistoryRepositoryPort;
import org.sopt.makers.storage.db.playground.post.entity.MumuPostWriteHistoryEntity;
import org.sopt.makers.storage.db.playground.post.repository.MumuPostWriteHistoryJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MumuPostWriteHistoryRepositoryAdapter implements MumuPostWriteHistoryRepositoryPort {

  private final MumuPostWriteHistoryJpaRepository repository;

  @Override
  public MumuPostWriteHistory save(MumuPostWriteHistory history) {
    return repository.save(MumuPostWriteHistoryEntity.fromDomain(history)).toDomain();
  }

  @Override
  public boolean existsByUserIdAndWrittenDate(Long userId, LocalDate writtenDate) {
    return repository.existsByUserIdAndWrittenDate(userId, writtenDate);
  }
}
