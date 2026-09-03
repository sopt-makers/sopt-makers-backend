package org.sopt.makers.storage.db.playground.post.repository;

import java.time.LocalDate;
import org.sopt.makers.storage.db.playground.post.entity.MumuPostWriteHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MumuPostWriteHistoryJpaRepository
    extends JpaRepository<MumuPostWriteHistoryEntity, Long> {

  boolean existsByUserIdAndWrittenDate(Long userId, LocalDate writtenDate);
}
