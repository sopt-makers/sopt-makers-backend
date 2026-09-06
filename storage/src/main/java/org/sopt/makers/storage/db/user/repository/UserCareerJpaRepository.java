package org.sopt.makers.storage.db.user.repository;

import java.util.List;
import org.sopt.makers.storage.db.user.entity.UserCareerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCareerJpaRepository extends JpaRepository<UserCareerEntity, Long> {

  @Modifying
  @Query("delete from UserCareerEntity c where c.userId = :userId")
  void deleteAllByUserId(@Param("userId") Long userId);

  List<UserCareerEntity> findAllByUserId(Long userId);

  List<UserCareerEntity> findAllByUserIdIn(List<Long> userIds);
}
