package org.sopt.makers.domain.playground.resolution.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.resolution.UserResolutionLuckyPick;

public interface UserResolutionLuckyPickRepositoryPort {

  Optional<UserResolutionLuckyPick> findByUserId(Long userId);

  boolean existsByUserIdAndHasDrawnTrue(Long userId);

  long count();

  UserResolutionLuckyPick save(UserResolutionLuckyPick luckyPick);

  List<UserResolutionLuckyPick> saveAll(List<UserResolutionLuckyPick> luckyPicks);
}
