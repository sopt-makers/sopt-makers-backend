package org.sopt.makers.domain.playground.resolution.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.resolution.port.ResolutionUserPort;
import org.sopt.makers.domain.user.port.PlaygroundResolutionUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResolutionUserAdapter implements ResolutionUserPort {

    private final PlaygroundResolutionUserPort playgroundResolutionUserPort;

    @Override
    public boolean existsById(Long userId) {
        return playgroundResolutionUserPort.existsById(userId);
    }

    @Override
    public boolean hasActivities(Long userId) {
        return playgroundResolutionUserPort.hasActivities(userId);
    }

    @Override
    public int getLastGeneration(Long userId) {
        return playgroundResolutionUserPort.getLastGeneration(userId);
    }
}
