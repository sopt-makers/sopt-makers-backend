package org.sopt.makers.domain.user.port;

import java.util.OptionalInt;
import java.util.Set;

public interface AppSoptLetterUserPort {

  Set<Long> findSoptUserIdsByGeneration(int generation);

  OptionalInt findLastSoptGeneration(Long userId);
}
