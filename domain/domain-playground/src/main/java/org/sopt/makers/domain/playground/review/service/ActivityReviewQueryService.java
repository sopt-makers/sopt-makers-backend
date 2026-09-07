package org.sopt.makers.domain.playground.review.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.port.CurrentGenerationProvider;
import org.sopt.makers.domain.playground.review.port.ActivityReviewRepositoryPort;
import org.sopt.makers.domain.playground.review.port.ActivityReviewRepositoryPort.ActivityReviewPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityReviewQueryService {

  private final ActivityReviewRepositoryPort activityReviewRepositoryPort;
  private final CurrentGenerationProvider currentGenerationProvider;

  public ActivityReviewPage getActivityReviews(int page, int size) {
    return activityReviewRepositoryPort.findAllByGeneration(
        currentGenerationProvider.getCurrentGeneration(), page, size);
  }
}
