package org.sopt.makers.domain.playground.review.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.review.ActivityReview;
import org.sopt.makers.domain.playground.review.exception.ActivityReviewException;
import org.sopt.makers.domain.playground.review.exception.ActivityReviewFailure;
import org.sopt.makers.domain.playground.review.port.ActivityReviewRepositoryPort;
import org.sopt.makers.domain.playground.review.port.ActivityReviewUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityReviewCommandService {

  // TODO: 새 기수 시작 전 값 변경 필수
  private static final int CURRENT_GENERATION = 38;

  private final ActivityReviewRepositoryPort activityReviewRepositoryPort;
  private final ActivityReviewUserPort activityReviewUserPort;

  @Transactional
  public void createActivityReview(Long userId, String content) {
    validateCurrentGeneration(userId);
    activityReviewRepositoryPort.save(
        ActivityReview.create(userId, content, CURRENT_GENERATION));
  }

  private void validateCurrentGeneration(Long userId) {
    if (activityReviewUserPort.getLastGeneration(userId) != CURRENT_GENERATION) {
      throw new ActivityReviewException(ActivityReviewFailure.NOT_CURRENT_GENERATION);
    }
  }
}
