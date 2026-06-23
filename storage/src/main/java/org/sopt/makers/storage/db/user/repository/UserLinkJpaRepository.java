package org.sopt.makers.storage.db.user.repository;

import org.sopt.makers.storage.db.user.entity.UserLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserLinkJpaRepository extends JpaRepository<UserLinkEntity, Long> {

  @Modifying
  @Query("delete from UserLinkEntity l where l.userId = :userId")
  void deleteAllByUserId(@Param("userId") Long userId);
}
