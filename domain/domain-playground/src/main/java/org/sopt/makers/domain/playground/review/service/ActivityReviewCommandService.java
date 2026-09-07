package org.sopt.makers.domain.playground.review.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.port.CurrentGenerationProvider;
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

  private final ActivityReviewRepositoryPort activityReviewRepositoryPort;
  private final ActivityReviewUserPort activityReviewUserPort;
  private final CurrentGenerationProvider currentGenerationProvider;

  @Transactional
  public void createActivityReview(Long userId, String content) {
    validateCurrentGeneration(userId);
    activityReviewRepositoryPort.save(
        ActivityReview.create(userId, content, currentGenerationProvider.getCurrentGeneration()));
  }

  private void validateCurrentGeneration(Long userId) {
    if (activityReviewUserPort.getLastGeneration(userId) != currentGenerationProvider.getCurrentGeneration()) {
      throw new ActivityReviewException(ActivityReviewFailure.NOT_CURRENT_GENERATION);
    }
  }
}
