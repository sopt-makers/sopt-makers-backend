package org.sopt.makers.storage.db.app.soptamp.user.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.app.soptamp.user.entity.SoptampUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SoptampUserJpaRepository extends JpaRepository<SoptampUserEntity, Long> {

  @Query("SELECT u.userId FROM SoptampUserEntity u")
  List<Long> findAllUserIds();

  Optional<SoptampUserEntity> findByUserId(Long userId);

  List<SoptampUserEntity> findAllByUserIdIn(Collection<Long> userIds);

  List<SoptampUserEntity> findAllByGeneration(Long generation);

  Optional<SoptampUserEntity> findByNickname(String nickname);

  boolean existsByNickname(String nickname);

  boolean existsByNicknameAndUserIdNot(String nickname, Long userId);

  void deleteByUserId(Long userId);
}
