package org.sopt.makers.storage.db.crew.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import org.sopt.makers.storage.db.crew.entity.SlackEmojiMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SlackEmojiMappingJpaRepository
    extends JpaRepository<SlackEmojiMappingEntity, Long> {

  boolean existsByCallEmojiAndUserSlackId(String callEmoji, String userSlackId);

  List<SlackEmojiMappingEntity> findAllByCallEmojiOrderByIdAsc(String callEmoji);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT mapping FROM SlackEmojiMappingEntity mapping
      WHERE mapping.callEmoji = :callEmoji
      ORDER BY mapping.id ASC
      """)
  List<SlackEmojiMappingEntity> findAllByCallEmojiForUpdate(@Param("callEmoji") String callEmoji);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      UPDATE SlackEmojiMappingEntity mapping
      SET mapping.callEmoji = :updatedCallEmoji
      WHERE mapping.callEmoji = :originalCallEmoji
      """)
  void updateCallEmoji(
      @Param("originalCallEmoji") String originalCallEmoji,
      @Param("updatedCallEmoji") String updatedCallEmoji);

  void deleteAllByCallEmoji(String callEmoji);
}
