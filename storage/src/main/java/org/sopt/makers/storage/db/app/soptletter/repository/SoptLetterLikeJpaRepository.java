package org.sopt.makers.storage.db.app.soptletter.repository;

import java.util.Collection;
import java.util.List;
import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterLikeJpaRepository extends JpaRepository<SoptLetterLikeEntity, Long> {

  boolean existsByLetterIdAndUserId(Long letterId, Long userId);

  @Query(
      "SELECT l.letterId FROM SoptLetterLikeEntity l"
          + " WHERE l.userId = :userId AND l.letterId IN :letterIds")
  List<Long> findLikedLetterIds(
      @Param("userId") Long userId, @Param("letterIds") Collection<Long> letterIds);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          "INSERT INTO sopt_letter_like (user_id, letter_id, created_at, updated_at)"
              + " VALUES (:userId, :letterId, now(), now())"
              + " ON CONFLICT (letter_id, user_id) DO NOTHING",
      nativeQuery = true)
  int addIfAbsent(@Param("userId") Long userId, @Param("letterId") Long letterId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  int deleteByLetterIdAndUserId(Long letterId, Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM SoptLetterLikeEntity l WHERE l.letterId = :letterId")
  void deleteAllByLetterId(@Param("letterId") Long letterId);
}
