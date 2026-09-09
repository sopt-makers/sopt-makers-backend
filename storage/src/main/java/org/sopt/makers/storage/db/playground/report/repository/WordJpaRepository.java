package org.sopt.makers.storage.db.playground.report.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.storage.db.playground.report.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordJpaRepository extends JpaRepository<WordEntity, Long> {

  List<WordEntity> findAllByMemberIdAndCreatedAtBetween(
      Long memberId, LocalDateTime start, LocalDateTime end);
}
