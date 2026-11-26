package org.sopt.makers.storage.db.app.soptletter.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterTopicJpaRepository extends JpaRepository<SoptLetterTopicEntity, Long> {

  @Query(
      "SELECT t FROM SoptLetterTopicEntity t"
          + " WHERE t.startedAt <= :now AND t.endedAt >= :now"
          + " ORDER BY t.isDefault DESC, t.createdAt DESC NULLS LAST")
  List<SoptLetterTopicEntity> findActiveDefaultFirst(@Param("now") LocalDateTime now);

  @Query(
      "SELECT t FROM SoptLetterTopicEntity t"
          + " WHERE t.isDefault = :isDefault AND t.startedAt <= :now AND t.endedAt >= :now"
          + " ORDER BY t.createdAt DESC NULLS LAST")
  List<SoptLetterTopicEntity> findActiveByIsDefault(
      @Param("isDefault") boolean isDefault, @Param("now") LocalDateTime now);

  List<SoptLetterTopicEntity> findAllByIsDefaultOrderByCreatedAtDesc(boolean isDefault);

  boolean existsByIsDefaultFalse();

  @Query(
      "SELECT t FROM SoptLetterTopicEntity t"
          + " WHERE t.ctaText IS NOT NULL AND t.startedAt <= :now AND t.endedAt >= :now"
          + " ORDER BY t.startedAt DESC, t.id DESC")
  List<SoptLetterTopicEntity> findActiveCtas(@Param("now") LocalDateTime now);
}
