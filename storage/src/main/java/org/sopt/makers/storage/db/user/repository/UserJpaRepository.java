package org.sopt.makers.storage.db.user.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

  @Query("SELECT u.id FROM UserEntity u")
  List<Long> findAllUserIds();

  @Query(
      "SELECT u FROM UserEntity u"
          + " WHERE u.authPlatformType = :type AND u.authPlatformId = :platformId")
  Optional<UserEntity> findByAuthPlatform(
      @Param("type") OAuthPlatform type, @Param("platformId") String platformId);

  Optional<UserEntity> findByPhone(String phone);

  boolean existsByPhone(String phone);

  @Query("SELECT u.id FROM UserEntity u WHERE u.id IN :userIds")
  List<Long> findExistingIds(@Param("userIds") Collection<Long> userIds);
}
