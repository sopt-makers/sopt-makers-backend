package org.sopt.makers.domain.user.port;

import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.user.User;

public interface AppSoptLetterUserPort {

  Set<Long> findSoptUserIdsByGeneration(int generation);

  Optional<User> findWithActivities(Long userId);
}
