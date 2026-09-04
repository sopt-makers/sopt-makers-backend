package org.sopt.makers.domain.playground.review.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.review.port.ActivityReviewRepositoryPort;
import org.sopt.makers.domain.playground.review.port.ActivityReviewRepositoryPort.ActivityReviewPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityReviewQueryService {

  // TODO: 새 기수 시작 전 값 변경 필수
  private static final int CURRENT_GENERATION = 38;

  private final ActivityReviewRepositoryPort activityReviewRepositoryPort;

  public ActivityReviewPage getActivityReviews(int page, int size) {
    return activityReviewRepositoryPort.findAllByGeneration(CURRENT_GENERATION, page, size);
  }
}
