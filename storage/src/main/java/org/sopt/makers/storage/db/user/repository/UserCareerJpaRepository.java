package org.sopt.makers.storage.db.user.repository;

import org.sopt.makers.storage.db.user.entity.UserCareerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCareerJpaRepository extends JpaRepository<UserCareerEntity, Long> {

  @Modifying
  @Query("delete from UserCareerEntity c where c.userId = :userId")
  void deleteAllByUserId(@Param("userId") Long userId);
}
