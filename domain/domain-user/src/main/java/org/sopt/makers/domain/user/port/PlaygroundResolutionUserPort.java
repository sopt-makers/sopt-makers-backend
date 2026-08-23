package org.sopt.makers.domain.user.port;

public interface PlaygroundResolutionUserPort {

    boolean existsById(Long userId);

    boolean hasActivities(Long userId);

    int getLastGeneration(Long userId);
}
