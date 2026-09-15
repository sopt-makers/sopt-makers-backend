package org.sopt.makers.storage.db.playground.review.repository;

import org.sopt.makers.storage.db.playground.review.entity.ActivityReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityReviewJpaRepository extends JpaRepository<ActivityReviewEntity, Long> {

  Page<ActivityReviewEntity> findAllByGeneration(int generation, Pageable pageable);
}
