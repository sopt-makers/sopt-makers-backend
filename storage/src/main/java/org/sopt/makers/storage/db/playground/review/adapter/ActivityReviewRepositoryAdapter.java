package org.sopt.makers.storage.db.playground.review.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.review.ActivityReview;
import org.sopt.makers.domain.playground.review.port.ActivityReviewRepositoryPort;
import org.sopt.makers.storage.db.playground.review.entity.ActivityReviewEntity;
import org.sopt.makers.storage.db.playground.review.repository.ActivityReviewJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityReviewRepositoryAdapter implements ActivityReviewRepositoryPort {

  private final ActivityReviewJpaRepository activityReviewJpaRepository;

  @Transactional
  @Override
  public ActivityReview save(ActivityReview review) {
    return activityReviewJpaRepository.save(ActivityReviewEntity.from(review)).toDomain();
  }

  @Override
  public ActivityReviewPage findAllByGeneration(int generation, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ActivityReviewEntity> result =
        activityReviewJpaRepository.findAllByGeneration(generation, pageRequest);
    List<ActivityReview> reviews =
        result.getContent().stream().map(ActivityReviewEntity::toDomain).toList();
    return new ActivityReviewPage(reviews, result.hasNext());
  }
}
