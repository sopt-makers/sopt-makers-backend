package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.domain.official.homepage.review.HomepageReviewType;
import org.sopt.makers.storage.db.official.entity.HomepageReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomepageReviewJpaRepository extends JpaRepository<HomepageReviewEntity, Long> {

  long countByType(HomepageReviewType type);

  List<HomepageReviewEntity> findAllByOrderByIdAsc();
}
