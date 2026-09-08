package org.sopt.makers.storage.db.playground.community.anonymous.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousNicknameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnonymousNicknameJpaRepository
    extends JpaRepository<AnonymousNicknameEntity, Long> {

  @Query(
      value =
          "SELECT * FROM anonymous_nickname WHERE anonymous_nickname_id NOT IN (:excludes) ORDER BY RANDOM() LIMIT 1",
      nativeQuery = true)
  AnonymousNicknameEntity findRandomOneByIdNotIn(@Param("excludes") List<Long> excludes);

  @Query(value = "SELECT * FROM anonymous_nickname ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
  AnonymousNicknameEntity findRandomOne();

  List<AnonymousNicknameEntity> findAllByNicknameIn(List<String> nicknames);

  List<AnonymousNicknameEntity> findAllByIdIn(List<Long> ids);
}
