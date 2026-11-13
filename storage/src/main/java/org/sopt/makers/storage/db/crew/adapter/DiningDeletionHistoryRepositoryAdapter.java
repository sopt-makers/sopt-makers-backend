package org.sopt.makers.storage.db.crew.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.model.DiningDeletionHistory;
import org.sopt.makers.domain.crew.dining.port.DiningDeletionHistoryPort;
import org.sopt.makers.storage.db.crew.entity.DiningDeletionHistoryEntity;
import org.sopt.makers.storage.db.crew.repository.DiningDeletionHistoryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class DiningDeletionHistoryRepositoryAdapter implements DiningDeletionHistoryPort {
  private final DiningDeletionHistoryJpaRepository repository;

  @Override
  @Transactional
  public void save(DiningDeletionHistory history) {
    repository.save(DiningDeletionHistoryEntity.fromDomain(history));
  }
}
