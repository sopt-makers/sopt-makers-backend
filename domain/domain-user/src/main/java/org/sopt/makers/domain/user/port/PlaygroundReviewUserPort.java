package org.sopt.makers.domain.user.port;

public interface PlaygroundReviewUserPort {

  boolean existsById(Long userId);

  int getLastGeneration(Long userId);
}
