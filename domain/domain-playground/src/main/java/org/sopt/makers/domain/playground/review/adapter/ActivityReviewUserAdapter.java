package org.sopt.makers.domain.playground.review.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.review.port.ActivityReviewUserPort;
import org.sopt.makers.domain.user.port.PlaygroundReviewUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityReviewUserAdapter implements ActivityReviewUserPort {

  private final PlaygroundReviewUserPort playgroundReviewUserPort;

  @Override
  public boolean existsById(Long userId) {
    return playgroundReviewUserPort.existsById(userId);
  }

  @Override
  public int getLastGeneration(Long userId) {
    return playgroundReviewUserPort.getLastGeneration(userId);
  }
}
