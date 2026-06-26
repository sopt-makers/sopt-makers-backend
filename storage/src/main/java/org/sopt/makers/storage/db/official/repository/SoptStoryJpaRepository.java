package org.sopt.makers.storage.db.official.repository;

import org.sopt.makers.storage.db.official.entity.SoptStoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptStoryJpaRepository extends JpaRepository<SoptStoryEntity, Long> {

  boolean existsByUrl(String url);

  Page<SoptStoryEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

  Page<SoptStoryEntity> findAllByOrderByLikeCountDesc(Pageable pageable);
}
