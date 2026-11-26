package org.sopt.makers.storage.db.app.soptletter.repository;

import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterLikeNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterLikeNotificationJpaRepository
    extends JpaRepository<SoptLetterLikeNotificationEntity, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          "INSERT INTO sopt_letter_like_notification (user_id, letter_id, created_at, updated_at)"
              + " VALUES (:userId, :letterId, now(), now())"
              + " ON CONFLICT (letter_id, user_id) DO NOTHING",
      nativeQuery = true)
  int addIfAbsent(@Param("userId") Long userId, @Param("letterId") Long letterId);
}
