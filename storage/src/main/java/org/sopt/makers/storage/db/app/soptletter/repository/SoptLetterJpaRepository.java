package org.sopt.makers.storage.db.app.soptletter.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterJpaRepository extends JpaRepository<SoptLetterEntity, Long> {

  List<SoptLetterEntity> findAllByTopicIdOrderByIdDesc(Long topicId, Pageable pageable);

  List<SoptLetterEntity> findAllByTopicIdAndIdLessThanOrderByIdDesc(
      Long topicId, Long cursor, Pageable pageable);

  Optional<SoptLetterEntity> findFirstByTopicIdOrderByIdDesc(Long topicId);

  long countByTopicId(Long topicId);

  long countByAuthorProfileIdAndCreatedAtGreaterThanEqual(
      Long authorProfileId, LocalDateTime since);

  boolean existsByIdAndTopicId(Long id, Long topicId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE SoptLetterEntity l SET l.message = :message, l.updatedAt = CURRENT_TIMESTAMP"
          + " WHERE l.id = :letterId")
  void updateMessage(@Param("letterId") Long letterId, @Param("message") String message);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE SoptLetterEntity l SET l.likeCount = l.likeCount + 1 WHERE l.id = :letterId")
  void increaseLikeCount(@Param("letterId") Long letterId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE SoptLetterEntity l SET l.likeCount = l.likeCount - 1"
          + " WHERE l.id = :letterId AND l.likeCount > 0")
  void decreaseLikeCount(@Param("letterId") Long letterId);
}
