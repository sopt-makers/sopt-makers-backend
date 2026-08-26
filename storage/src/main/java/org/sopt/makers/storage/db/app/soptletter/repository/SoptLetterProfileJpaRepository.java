package org.sopt.makers.storage.db.app.soptletter.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterProfileJpaRepository
    extends JpaRepository<SoptLetterProfileEntity, Long> {

  Optional<SoptLetterProfileEntity> findByUserId(Long userId);

  boolean existsByUserId(Long userId);

  @Query("SELECT p.nickname FROM SoptLetterProfileEntity p WHERE p.nickname IN :nicknames")
  List<String> findExistingNicknames(@Param("nicknames") Collection<String> nicknames);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE SoptLetterProfileEntity p SET p.isOnboarded = true, p.updatedAt = CURRENT_TIMESTAMP"
          + " WHERE p.id = :profileId")
  void completeOnboarding(@Param("profileId") Long profileId);
}
