package org.sopt.makers.domain.playground.resolution.port;

public interface ResolutionUserPort {

    boolean existsById(Long userId);

    boolean hasActivities(Long userId);

    int getLastGeneration(Long userId);
}
