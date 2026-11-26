package org.sopt.makers.storage.db.app.soptletter.repository;

import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterTopicReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterTopicReadJpaRepository
    extends JpaRepository<SoptLetterTopicReadEntity, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          "INSERT INTO sopt_letter_topic_read"
              + " (user_id, topic_id, last_read_letter_id, created_at, updated_at)"
              + " VALUES (:userId, :topicId, :lastReadLetterId, now(), now())"
              + " ON CONFLICT (user_id, topic_id) DO UPDATE"
              + " SET last_read_letter_id = GREATEST("
              + "sopt_letter_topic_read.last_read_letter_id, EXCLUDED.last_read_letter_id),"
              + " updated_at = now()",
      nativeQuery = true)
  int upsertLastReadLetterId(
      @Param("userId") Long userId,
      @Param("topicId") Long topicId,
      @Param("lastReadLetterId") Long lastReadLetterId);
}
