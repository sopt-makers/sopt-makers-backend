package org.sopt.makers.storage.db.crew.repository;

import org.sopt.makers.storage.db.crew.entity.DiningDeletionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiningDeletionHistoryJpaRepository
    extends JpaRepository<DiningDeletionHistoryEntity, Long> {}
