package org.sopt.makers.domain.playground.review.port;

public interface ActivityReviewUserPort {

  boolean existsById(Long userId);

  int getLastGeneration(Long userId);
}
