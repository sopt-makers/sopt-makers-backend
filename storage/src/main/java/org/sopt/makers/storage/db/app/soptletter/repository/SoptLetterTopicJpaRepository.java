package org.sopt.makers.storage.db.app.soptletter.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterTopicJpaRepository extends JpaRepository<SoptLetterTopicEntity, Long> {

  List<SoptLetterTopicEntity> findAllByOrderByCreatedAtDesc();

  List<SoptLetterTopicEntity> findAllByIsDefaultOrderByCreatedAtDesc(boolean isDefault);

  boolean existsByIsDefaultFalse();

  @Query(
      "SELECT t FROM SoptLetterTopicEntity t"
          + " WHERE t.ctaText IS NOT NULL AND t.startedAt <= :now AND t.endedAt >= :now"
          + " ORDER BY t.createdAt DESC")
  List<SoptLetterTopicEntity> findActiveCtas(@Param("now") LocalDateTime now);
}
