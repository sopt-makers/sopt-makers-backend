package org.sopt.makers.domain.playground.resolution.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.resolution.UserResolution;

public interface UserResolutionRepositoryPort {

    Optional<UserResolution> findByUserIdAndGeneration(Long userId, int generation);

    boolean existsByUserIdAndGeneration(Long userId, int generation);

    List<UserResolution> findAllByGeneration(int generation);

    UserResolution save(UserResolution resolution);

    void delete(UserResolution resolution);
}
